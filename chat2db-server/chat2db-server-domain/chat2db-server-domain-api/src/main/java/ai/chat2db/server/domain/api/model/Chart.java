package ai.chat2db.server.domain.api.model;

import java.util.Date;

import lombok.Data;

/**
 * @author moji
 * @version Chart.java, v 0.1 2023年06月09日 15:37 moji Exp $
 * @date 2023/06/09
 */
@Data
public class Chart {

    /**
     * primary key
     */
    private Long id;

    /**
     * creation time
     */
    private Date gmtCreate;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Chart name
     */
    private String name;

    /**
     * Chart description
     */
    private String description;

    /**
     * schema
     */
    private String schema;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * Data source name
     */
    private String dataSourceName;

    /**
     * schema name
     */
    private String schemaName;

    /**
     * Database type
     */
    private String type;

    /**
     * db name
     */
    private String databaseName;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * Whether it has been deleted, y means deleted, n means not deleted
     */
    private String deleted;

    /**
     * user id
     */
    private Long userId;
}
