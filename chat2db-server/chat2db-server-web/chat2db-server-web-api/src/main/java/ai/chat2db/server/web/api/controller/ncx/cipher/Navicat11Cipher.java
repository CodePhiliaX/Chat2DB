package ai.chat2db.server.web.api.controller.ncx.cipher;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Navicat11及以下密码加密解密
 *
 * @author lzy
 */
public class Navicat11Cipher extends CommonCipher {
    public static final String DefaultUserKey = "3DC5CA39";
    private static byte[] IV;

    private static SecretKeySpec key;
    private static Cipher encryptor;
    private static Cipher decrypt;

    private static void initKey() {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            byte[] userKey_data = Navicat11Cipher.DefaultUserKey.getBytes(StandardCharsets.UTF_8);
            sha1.update(userKey_data, 0, userKey_data.length);
            key = new SecretKeySpec(sha1.digest(), "Blowfish");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initCipherEncrypt() {
        try {
            // Must use NoPadding
            encryptor = Cipher.getInstance("Blowfish/ECB/NoPadding");
            encryptor.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initCipherDecrypt() {
        try {
            // Must use NoPadding
            decrypt = Cipher.getInstance("Blowfish/ECB/NoPadding");
            decrypt.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initIV() {
        try {
            byte[] initVec = parseHexBinary("FFFFFFFFFFFFFFFF");
            IV = encryptor.doFinal(initVec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void xorBytes(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            int aVal = a[i] & 0xff; // convert byte to integer
            int bVal = b[i] & 0xff;
            a[i] = (byte) (aVal ^ bVal); // xor aVal and bVal and typecast to byte
        }
    }

    private void xorBytes(byte[] a, byte[] b, int l) {
        for (int i = 0; i < l; i++) {
            int aVal = a[i] & 0xff; // convert byte to integer
            int bVal = b[i] & 0xff;
            a[i] = (byte) (aVal ^ bVal); // xor aVal and bVal and typecast to byte
        }
    }

    static {
        initKey();
        initCipherEncrypt();
        initCipherDecrypt();
        initIV();
    }

    private byte[] Encrypt(byte[] inData) {
        try {
            byte[] CV = Arrays.copyOf(IV, IV.length);
            byte[] ret = new byte[inData.length];

            int blocks_len = inData.length / 8;
            int left_len = inData.length % 8;

            for (int i = 0; i < blocks_len; i++) {
                byte[] temp = Arrays.copyOfRange(inData, i * 8, (i * 8) + 8);

                xorBytes(temp, CV);
                temp = encryptor.doFinal(temp);
                xorBytes(CV, temp);

                System.arraycopy(temp, 0, ret, i * 8, 8);
            }

            if (left_len != 0) {
                CV = encryptor.doFinal(CV);
                byte[] temp = Arrays.copyOfRange(inData, blocks_len * 8, (blocks_len * 8) + left_len);
                xorBytes(temp, CV, left_len);
                System.arraycopy(temp, 0, ret, blocks_len * 8, temp.length);
            }

            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encryptString(String inputString) {
        try {
            byte[] inData = inputString.getBytes(StandardCharsets.UTF_8);
            byte[] outData = Encrypt(inData);
            return printHexBinary(outData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] Decrypt(byte[] inData) {
        try {
            byte[] cv = Arrays.copyOf(IV, IV.length);
            byte[] ret = new byte[inData.length];

            int blocks_len = inData.length / 8;
            int left_len = inData.length % 8;

            for (int i = 0; i < blocks_len; i++) {
                byte[] temp = Arrays.copyOfRange(inData, i * 8, (i * 8) + 8);

                temp = decrypt.doFinal(temp);
                xorBytes(temp, cv);
                System.arraycopy(temp, 0, ret, i * 8, 8);
                for (int j = 0; j < cv.length; j++) {
                    cv[j] = (byte) (cv[j] ^ inData[i * 8 + j]);
                }
            }

            if (left_len != 0) {
                cv = encryptor.doFinal(cv);
                byte[] temp = Arrays.copyOfRange(inData, blocks_len * 8, (blocks_len * 8) + left_len);

                xorBytes(temp, cv, left_len);
                for (int j = 0; j < temp.length; j++) {
                    ret[blocks_len * 8 + j] = temp[j];
                }
            }

            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decryptString(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            return "";
        }
        try {
            byte[] inData = parseHexBinary(hexString);
            byte[] outData = Decrypt(inData);
            return new String(outData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
