package ai.chat2db.server.domain.api.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * My execution record
 * </p>
 *
 * @author ali-dbhub
 * @since 2022-09-18
 */
@Data
public class OperationLog {

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
     * data source
     */
    private String dataSourceName;

    /**
     * db name
     */
    private String databaseName;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * status
     */
    private String status;

    /**
     * Number of operation lines
     */
    private Long operationRows;

    /**
     * Length of use
     */
    private Long useTime;

    /**
     * Extended Information
     */
    private String extendInfo;

    /**
     * schema name
     */
    private String schemaName;
}
