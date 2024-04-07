package ai.chat2db.server.domain.api.param.operation;

import lombok.Data;

/**
 * @author moji
 * @version UserSavedDdlCreateParam.java, v 0.1 September 25, 2022 15:40 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationSavedParam {

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;

    /**
     * save name
     */
    private String name;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl statement status: DRAFT/RELEASE
     */
    private String status;

    /**
     * ddl content
     */
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
