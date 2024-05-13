package ai.chat2db.server.web.api.controller.dashboard.vo;

import java.util.Date;

import lombok.Data;

/**
 * @author moji
 * @version Chart.java, v 0.1 June 9, 2023 15:37 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartVO {

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
     * chart information
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
     * Database type
     */
    private String type;

    /**
     * DB name
     */
    private String databaseName;


    /**
     * schema name
     */
    private String schemaName;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * Whether it can be connected, false means it cannot be connected, which means the data source has been deleted or does not exist.
     */
    private Boolean connectable;
}
