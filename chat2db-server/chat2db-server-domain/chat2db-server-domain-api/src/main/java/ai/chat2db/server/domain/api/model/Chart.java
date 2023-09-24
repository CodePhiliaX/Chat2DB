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
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表描述
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
     * 数据源名称
     */
    private String dataSourceName;

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
