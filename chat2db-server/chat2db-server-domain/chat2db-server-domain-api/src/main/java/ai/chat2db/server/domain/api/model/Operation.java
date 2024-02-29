package ai.chat2db.server.domain.api.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * My save list
 * </p>
 *
 * @author ali-dbhub
 * @since 2022-09-18
 */
@Data
public class Operation {

    /**
     * primary key
     */
    private Long id;

    /**
     * creation time
     */
    private LocalDateTime gmtCreate;

    /**
     * modified time
     */
    private LocalDateTime gmtModified;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * Data source name
     */
    private String dataSourceName;

    /**
     * db name
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

    /**
     * user id
     */
    private Long userId;
}
