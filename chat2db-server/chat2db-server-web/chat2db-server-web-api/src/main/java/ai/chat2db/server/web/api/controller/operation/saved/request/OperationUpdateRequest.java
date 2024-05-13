package ai.chat2db.server.web.api.controller.operation.saved.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 September 18, 2022 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationUpdateRequest {

    /**
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * file alias
     */
    private String name;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl content
     */
    @NotNull
    private String ddl;

    /**
     * Update status DRAFT/RELEASE
     */
    private String status;

    /**
     * Whether it is opened in the tab, y means open, n means not opened
     */
    private String tabOpened;

    /**
     * operation type
     */
    private String operationType;

    /**
     * user id
     */
    private Long userId;
}
