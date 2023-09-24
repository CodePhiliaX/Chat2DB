package ai.chat2db.server.web.api.controller.ncx.factory;

import ai.chat2db.server.web.api.controller.ncx.cipher.CommonCipher;
import ai.chat2db.server.web.api.controller.ncx.cipher.Navicat11Cipher;
import ai.chat2db.server.web.api.controller.ncx.cipher.Navicat12Cipher;
import ai.chat2db.server.web.api.controller.ncx.enums.VersionEnum;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CipherFactory
 *
 * @author lzy
 **/
@Service
public class CipherFactory {
    /**
     * NavicatCipher缓存池
     */
    private static final Map<String, CommonCipher> REPORT_POOL = new ConcurrentHashMap<>(0);

    static {
        REPORT_POOL.put(VersionEnum.native11.name(), new Navicat11Cipher());
        REPORT_POOL.put(VersionEnum.navicat12more.name(), new Navicat12Cipher());
    }

    /**
     * 获取对应加/解密方法
     *
     * @param type 类型
     * @return ITokenGranter
     */
    @SneakyThrows
    public static CommonCipher get(String type) {
        CommonCipher cipher = REPORT_POOL.get(type);
        if (cipher == null) {
            throw new ClassNotFoundException("no CommonCipher was found");
        } else {
            return cipher;
        }
    }
}
