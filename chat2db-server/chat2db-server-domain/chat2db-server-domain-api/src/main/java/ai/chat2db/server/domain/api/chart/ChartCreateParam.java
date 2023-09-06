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
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 图表名称
     */
    private String name;

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

    /**
     * 是否被删除,y表示删除,n表示未删除
     */
    private String deleted;

    /**
     * 用户id
     */
    private Long userId;
}
