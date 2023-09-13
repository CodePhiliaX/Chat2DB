package ai.chat2db.server.web.api.controller.dashboard.request;

import lombok.Data;

/**
 * @author moji
 * @version ChartCreateParam.java, v 0.1 2023年06月09日 15:38 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartCreateRequest {


    /**
     * 图表名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图表信息
     */
    private String schema;

    /**
     * 数据源连接ID
     */
    private Long dataSourceId;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * db名称
     */
    private String databaseName;

    /**
     * ddl内容
     */
    private String ddl;

}
