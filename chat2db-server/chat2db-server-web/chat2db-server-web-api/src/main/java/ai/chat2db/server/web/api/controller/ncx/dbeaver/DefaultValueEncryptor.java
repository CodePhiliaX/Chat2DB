/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.chat2db.server.web.api.controller.ncx.dbeaver;

import org.apache.poi.util.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Default value encryptor.
 *
 * Uses Eclipse secure preferences to read/write secrets.
 */
public class DefaultValueEncryptor implements DBSValueEncryptor {

    public static final String CIPHER_NAME = "AES/CBC/PKCS5Padding";
    public static final String KEY_ALGORITHM = "AES";

    private static final byte[] LOCAL_KEY_CACHE = new byte[] { -70, -69, 74, -97, 119, 74, -72, 83, -55, 108, 45, 101, 61, -2, 84, 74 };

    private final SecretKey secretKey;
    private final Cipher cipher;

    public DefaultValueEncryptor(SecretKey secretKey) {
        this.secretKey = secretKey;
        try {
            this.cipher = Cipher.getInstance(CIPHER_NAME);
        } catch (Exception e) {
            throw new IllegalStateException("Internal error during encrypted init", e);
        }
    }

    /**
     * 通过 DBeaver 源码查看到默认的 SecretKey
     **/
    public static SecretKey getLocalSecretKey() {
        return new SecretKeySpec(LOCAL_KEY_CACHE, DefaultValueEncryptor.KEY_ALGORITHM);
    }

    public static SecretKey makeSecretKeyFromPassword(String password) {
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] passBytes = Arrays.copyOf(bytes, 16);
        return new SecretKeySpec(passBytes, KEY_ALGORITHM);
    }

    @Override
    public byte[] encryptValue(byte[] value) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();

            ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
            try (CipherOutputStream cipherOut = new CipherOutputStream(resultBuffer, cipher)) {
                resultBuffer.write(iv);
                cipherOut.write(value);
            }
            return resultBuffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting value", e);
        }
    }

    @Override
    public byte[] decryptValue(byte[] value) {
        try (InputStream byteStream = new ByteArrayInputStream(value)) {
            byte[] fileIv = new byte[16];
            byteStream.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

            try (CipherInputStream cipherIn = new CipherInputStream(byteStream, cipher)) {
                ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
                IOUtils.copy(cipherIn, resultBuffer);
                return resultBuffer.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting value", e);
        }
    }

}