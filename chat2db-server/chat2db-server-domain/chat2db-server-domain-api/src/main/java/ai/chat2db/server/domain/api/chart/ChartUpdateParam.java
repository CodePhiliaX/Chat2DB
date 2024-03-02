package ai.chat2db.server.domain.api.chart;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version ChartUpdateParam.java, v 0.1 June 9, 2023 15:39 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartUpdateParam {

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
     * chart name
     */
    private String name;

    /**
     * chart information
     */
    private String schema;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

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
     * user id
     */
    private Long userId;
}
