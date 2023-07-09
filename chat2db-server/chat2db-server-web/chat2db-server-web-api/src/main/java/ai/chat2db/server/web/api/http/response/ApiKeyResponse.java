package ai.chat2db.server.web.api.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * apikey
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponse {

    /**
     * key
     */
    private String key;

    /**
     * Expiration time
     */
    private Long expiry;

    /**
     * Number of uses
     */
    private Long remainingUses;

    /**
     * WeChat official account url
     */
    private String wechatMpUrl;

    /**
     * https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET
     */
    private String wechatQrCodeUrl;
}
