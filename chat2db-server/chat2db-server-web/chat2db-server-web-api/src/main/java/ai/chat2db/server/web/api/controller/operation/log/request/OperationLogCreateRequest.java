package ai.chat2db.server.web.api.controller.operation.log.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 September 18, 2022 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogCreateRequest extends DataSourceBaseRequest {

    /**
     * file alias
     */
    private String name;

    /**
     * ddl type
     */
    @NotNull
    private String type;

    /**
     * ddl content
     */
    @NotNull
    private String ddl;
}
