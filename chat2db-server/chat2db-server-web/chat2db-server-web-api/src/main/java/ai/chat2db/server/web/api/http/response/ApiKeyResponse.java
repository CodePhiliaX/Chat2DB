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
     * Expiration
     */
    private Long expiry;

    /**
     * return
     */
    private Long remainingUses;

    /**
     * WeChat public account url
     */
    private String wechatMpUrl;
}
