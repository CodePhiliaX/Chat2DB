package com.alibaba.dbhub.server.web.api.controller.dashboard.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version Dashboard.java, v 0.1 2023年06月09日 15:32 moji Exp $
 * @date 2023/06/09
 */
@Data
public class DashboardVO {

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
     * 报表名称
     */
    private String name;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 报表布局信息
     */
    private String schema;

}
