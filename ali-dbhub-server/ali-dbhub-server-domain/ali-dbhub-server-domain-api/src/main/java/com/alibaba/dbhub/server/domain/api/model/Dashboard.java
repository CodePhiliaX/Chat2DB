package com.alibaba.dbhub.server.domain.api.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author moji
 * @version Dashboard.java, v 0.1 2023年06月09日 15:32 moji Exp $
 * @date 2023/06/09
 */
@Data
public class Dashboard {

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

    /**
     * 是否被删除,y表示删除,n表示未删除
     */
    private String deleted;

    /**
     * 用户id
     */
    private Long userId;

}
