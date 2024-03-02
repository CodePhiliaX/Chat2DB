package ai.chat2db.server.web.api.controller.redis.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version TableVO.java, v 0.1 September 16, 2022 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
public class KeyCreateRequest extends DataSourceBaseRequest {

    /**
     * key name
     */
    @NotNull
    private String name;

    /**
     * key value
     */
    private Object value;

    /**
     * Expiration
     */
    private Long ttl;
}
