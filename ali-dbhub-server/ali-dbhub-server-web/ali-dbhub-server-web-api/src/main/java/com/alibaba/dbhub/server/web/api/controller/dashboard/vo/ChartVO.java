package com.alibaba.dbhub.server.web.api.controller.dashboard.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version Chart.java, v 0.1 2023年06月09日 15:37 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartVO {

    /**
     * 主键
     */
    private Long id;

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
     * 是否可连接，false表示不可连接，表示数据源已被删除或不存在
     */
    private Boolean connectable;
}
