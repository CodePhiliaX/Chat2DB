package ai.chat2db.server.web.api.controller.operation.saved.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.tools.base.enums.StatusEnum;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 September 18, 2022 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationCreateRequest extends DataSourceBaseRequest {

    /**
     * file alias
     */
    private String name;

    /**
     * Save state
     * @see StatusEnum
     */
    @NotNull
    private String status;

    /**
     * DB TYPE
     */
    @NotNull
    private String type;

    /**
     * ddl content
     */
    @NotNull
    private String ddl;

    /**
     * Whether it is opened in the tab, y means open, n means not opened
     */
    private String tabOpened;

    /**
     * operation type
     */
    private String operationType;
}
