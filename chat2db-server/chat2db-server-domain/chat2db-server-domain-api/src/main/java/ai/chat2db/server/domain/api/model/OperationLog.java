package ai.chat2db.server.domain.api.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * 我的执行记录
 * </p>
 *
 * @author ali-dbhub
 * @since 2022-09-18
 */
@Data
public class OperationLog {

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
     * 数据源
     */
    private String dataSourceName;

    /**
     * db名称
     */
    private String databaseName;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * ddl内容
     */
    private String ddl;



    /**
     * 状态
     */
    private String status;

    /**
     * 操作行数
     */
    private Long operationRows;

    /**
     * 使用时长
     */
    private Long useTime;

    /**
     * 扩展信息
     */
    private String extendInfo;

    /**
     * schema名称
     */
    private String schemaName;
}
