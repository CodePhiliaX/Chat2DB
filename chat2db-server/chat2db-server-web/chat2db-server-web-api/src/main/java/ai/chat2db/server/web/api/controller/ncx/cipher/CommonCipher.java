package ai.chat2db.server.web.api.controller.ncx.cipher;

import java.util.Formatter;

/**
 * CommonCipher 公共加/解密
 *
 * @author lzy
 */
public abstract class CommonCipher {

    public String encryptString(String plaintext) {
        return null;
    }

    public String decryptString(String ciphertext) {
        return null;
    }

    public String printHexBinary(byte[] data) {
        StringBuilder hexBuilder = new StringBuilder();
        Formatter formatter = new Formatter(hexBuilder);
        for (byte b : data) {
            formatter.format("%02x", b);
        }
        return hexBuilder.toString();
    }

    public static byte[] parseHexBinary(String data) {
        return hexStringToByteArray(data);
    }

    public static byte[] hexStringToByteArray(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even");
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            String byteString = hex.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(byteString, 16);
        }
        return bytes;
    }
}
