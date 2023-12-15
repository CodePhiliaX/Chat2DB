package ai.chat2db.server.web.api.controller.ncx.cipher;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Navicat12及以上密码加密解密
 *
 * @author lzy
 */
public class Navicat12Cipher extends CommonCipher {
    private static final SecretKeySpec AES_KEY;
    private static final IvParameterSpec AES_IV;

    static {
        AES_KEY = new SecretKeySpec("libcckeylibcckey".getBytes(StandardCharsets.UTF_8), "AES");
        AES_IV = new IvParameterSpec("libcciv libcciv ".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encryptString(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, AES_KEY, AES_IV);
            byte[] ret = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return printHexBinary(ret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decryptString(String ciphertext) {
        if (StringUtils.isEmpty(ciphertext)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, AES_KEY, AES_IV);
            byte[] ret = cipher.doFinal(parseHexBinary(ciphertext));
            return new String(ret, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
