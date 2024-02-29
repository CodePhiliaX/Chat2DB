package ai.chat2db.server.domain.api.chart;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version ChartCreateParam.java, v 0.1 2023年06月09日 15:38 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartCreateParam {

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
     * db name
     */
    private String databaseName;

    /**
     * schemaName
     */
    private String schemaName;

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
