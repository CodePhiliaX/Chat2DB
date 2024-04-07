package ai.chat2db.server.web.api.controller.dashboard.request;

import lombok.Data;

/**
 * @author moji
 * @version ChartCreateParam.java, v 0.1 June 9, 2023 15:38 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartCreateRequest {


    /**
     * Chart name
     */
    private String name;

    /**
     * description
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
