package ai.chat2db.server.web.api.controller.redis.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class KeyQueryRequest extends DataSourceBaseRequest {

    /**
     * Cache key name
     */
    private String keyName;

    /**
     * search keyword
     */
    private String searchKey;
}
