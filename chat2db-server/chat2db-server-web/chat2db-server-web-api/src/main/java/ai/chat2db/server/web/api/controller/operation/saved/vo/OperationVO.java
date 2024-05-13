package ai.chat2db.server.web.api.controller.operation.saved.vo;


import lombok.Data;

/**
 * @author moji
 * @version DdlVO.java, v 0.1 September 18, 2022 11:06 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationVO {

    /**
     * primary key
     */
    private Long id;

    /**
     * file alias
     */
    private String name;

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * Data source name
     */
    private String dataSourceName;

    /**
     * Is it connectable?
     */
    private Boolean connectable;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;

    /**
     * ddl language type
     */
    private String type;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * ddl statement status: DRAFT/RELEASE
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
}
