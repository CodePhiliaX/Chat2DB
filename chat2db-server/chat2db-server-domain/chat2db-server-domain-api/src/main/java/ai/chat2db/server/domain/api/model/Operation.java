package ai.chat2db.server.domain.api.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * 我的保存表
 * </p>
 *
 * @author ali-dbhub
 * @since 2022-09-18
 */
@Data
public class Operation {

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
     * 数据源连接ID
     */
    private Long dataSourceId;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * db名称
     */
    private String databaseName;

    /**
     * 表所在空间
     */
    private String schemaName;
    
    /**
     * 保存名称
     */
    private String name;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * ddl语句状态:DRAFT/RELEASE
     */
    private String status;

    /**
     * ddl内容
     */
    private String ddl;

    /**
     * 是否在tab中被打开,y表示打开,n表示未打开
     */
    private String tabOpened;

    /**
     * operation type
     */
    private String operationType;

    /**
     * 用户id
     */
    private Long userId;
}
