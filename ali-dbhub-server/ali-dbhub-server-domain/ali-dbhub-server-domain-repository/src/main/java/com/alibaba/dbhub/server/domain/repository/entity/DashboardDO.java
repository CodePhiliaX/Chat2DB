package com.alibaba.dbhub.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 自定义报表表
 * </p>
 *
 * @author ali-dbhub
 * @since 2023-06-09
 */
@Getter
@Setter
@TableName("DASHBOARD")
public class DashboardDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
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
