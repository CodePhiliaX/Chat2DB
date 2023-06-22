package ai.chat2db.server.domain.core.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * @author moji
 * @version DESUtil.java, v 0.1 2022年12月26日 19:54 moji Exp $
 * @date 2022/12/26
 */
public class DesUtil {

    /**
     * CFB
     */
    public static final String CFB = "CFB";

    /**
     * OFB
     */
    public static final String OFB = "OFB";

    /**
     * CBC
     */
    public static final String CBC = "CBC";

    /**
     * iv向量
     */
    private static final byte[] DESIV = { (byte) 0xCE, (byte) 0x35, (byte) 0x5,
        (byte) 0xD, (byte) 0x98, (byte) 0x91, (byte) 0x8, (byte) 0xA };

    /**
     * AlgorithmParameterSpec
     */
    private static AlgorithmParameterSpec IV = null;

    /**
     * SHA1PRNG
     */
    private static final String SHA1PRNG = "SHA1PRNG";

    /**
     * DES默认模式
     */
    private static final String DES = "DES";

    /**
     * CBC加密模式
     */
    private static final String DES_CBC_PKCS5PADDING = "DES/CBC/PKCS5Padding";

    /**
     * OFB加密模式
     */
    private static final String DES_OFB_PKCS5PADDING = "DES/OFB/PKCS5Padding";

    /**
     * CFB加密模式
     */
    private static final String DES_CFB_PKCS5_PADDING = "DES/CFB/PKCS5Padding";

    /**
     * 加密模式
     */
    private static final int ENCRYPT_MODE = 1;

    /**
     * des key
     */
    public static final String DES_KEY = "dbhub";

    /**
     * 解密模式
     */
    private static final int DECRYPT_MODE = 2;

    /**
     * 密钥
     */
    private Key key;

    public DesUtil(String str) {
        getKey(str);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * 通过密钥获得key
     * @param secretKey 密钥
     * @author sucb
     * @date 2017年2月28日下午1:17:58
     */
    public void getKey(String secretKey) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
            secureRandom.setSeed(secretKey.getBytes());
            KeyGenerator generator = null;
            try {
                generator = KeyGenerator.getInstance(DES);
            } catch (NoSuchAlgorithmException e) {
            }
            generator.init(secureRandom);
            IV = new IvParameterSpec(DESIV);


            this.key = generator.generateKey();
            generator = null;

        } catch (Exception e) {
            throw new RuntimeException("Error in getKey(String secretKey), Cause: " + e);
        }
    }

    /**
     * 字符串des加密
     * @param data 需要加密的字符串
     * @param encryptType 加密模式 (ECB/CBC/OFB/CFB)
     * @return 加密结果
     * @throws Exception 异常
     * @author sucb
     * @date 2017年3月2日下午7:47:37
     */
    public String encrypt(String data, String encryptType) throws Exception {
        Cipher cipher = getPattern(encryptType, ENCRYPT_MODE);
        byte[] pasByte = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(pasByte);
    }

    /**
     * 字符串des解密
     * @param data 需要解密的字符串
     * @param decryptType  解密模式 (ECB/CBC/OFB/CFB)
     * @return 解密结果
     * @throws Exception 异常
     * @author sucb
     * @date 2017年3月2日下午7:48:21
     */
    public String decrypt(String data, String decryptType) throws Exception {
        if (StringUtils.isBlank(data)) {
            return Strings.EMPTY;
        }
        Cipher cipher = getPattern(decryptType, DECRYPT_MODE);
        byte[] pasByte = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(pasByte, "UTF-8");
    }

    /**
     * 初始化cipher
     * @param type  加密/解密模式 (ECB/CBC/OFB/CFB)
     * @param cipherMode cipher工作模式 1：加密； 2：解密
     * @return cipher
     * @throws Exception 异常
     * @author sucb
     * @date 2017年3月2日下午7:49:16
     */
    private Cipher getPattern(String type, int cipherMode) throws Exception {
        Cipher cipher;
        switch (type){
            case CBC :
                cipher = Cipher.getInstance(DES_CBC_PKCS5PADDING);
                cipher.init(cipherMode, key, IV);
                break;
            case OFB :
                cipher = Cipher.getInstance(DES_OFB_PKCS5PADDING);
                cipher.init(cipherMode, key, IV);
                break;
            case CFB :
                cipher = Cipher.getInstance(DES_CFB_PKCS5_PADDING);
                cipher.init(cipherMode, key, IV);
                break;
            default :
                cipher = Cipher.getInstance(DES);
                cipher.init(cipherMode, key);
                break;
        }
        return cipher;
    }

    /**
     * 文件 file 进行加密并保存目标文件 destFile 中
     * @param file 要加密的文件 如 c:/test/file.txt
     * @param destFile 加密后存放的文件名 如 c:/ 加密后文件 .txt
     * @param encryptType 加密模式 (ECB/CBC/OFB/CFB)
     * @return 加密结果   0：异常 1：加密成功； 5：未找到需要加密的文件
     * @author sucb
     * @date 2017年3月2日下午7:56:08
     */
    public int encryptFile(String file, String destFile, String encryptType) {
        int result = 0;
        try {
            Cipher cipher = getPattern(encryptType, ENCRYPT_MODE);
            InputStream is = new FileInputStream(file);
            OutputStream out = new FileOutputStream(destFile);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            cis.close();
            is.close();
            out.close();
            result = 1;
        } catch (FileNotFoundException e) {
            result = 5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 文件 file 进行解密并保存目标文件 destFile 中
     * @param file 要解密的文件 如 c:/test/file.txt
     * @param destFile 解密后存放的文件名 如 c:/ 解密后文件 .txt
     * @param decryptType 解密模式 (ECB/CBC/OFB/CFB)
     * @return 解密结果 0：解密异常；1：解密正常；5：未找到需要解密的文件
     * @author sucb
     * @date 2017年3月2日下午7:58:56
     */
    public int decryptFile(String file, String destFile, String decryptType) {
        int result = 0;
        try {
            Cipher cipher = getPattern(decryptType, DECRYPT_MODE);
            InputStream is = new FileInputStream(file);
            OutputStream out = new FileOutputStream(destFile);
            CipherOutputStream cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
            cos.close();
            out.close();
            is.close();
            result = 1;
        }catch (FileNotFoundException e) {
            result = 5;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
