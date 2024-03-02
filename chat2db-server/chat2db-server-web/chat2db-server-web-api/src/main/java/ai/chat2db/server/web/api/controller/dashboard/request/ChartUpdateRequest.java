package ai.chat2db.server.web.api.controller.dashboard.request;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version ChartUpdateParam.java, v 0.1 June 9, 2023 15:39 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartUpdateRequest {

    /**
     * primary key
     */
    private Long id;

    /**
     * Chart name
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

}
