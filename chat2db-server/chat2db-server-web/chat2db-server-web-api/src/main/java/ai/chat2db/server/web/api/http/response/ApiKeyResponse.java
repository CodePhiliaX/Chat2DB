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
     * 过期时间
     */
    private Long expiry;

    /**
     * 返回
     */
    private Long remainingUses;

    /**
     * 微信公众号url
     */
    private String wechatMpUrl;
}
