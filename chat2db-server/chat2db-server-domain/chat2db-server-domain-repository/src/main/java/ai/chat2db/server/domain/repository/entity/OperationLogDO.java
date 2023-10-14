package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 我的执行记录表
 * </p>
 *
 * @author chat2db
 * @since 2023-10-14
 */
@Getter
@Setter
@TableName("OPERATION_LOG")
public class OperationLogDO implements Serializable {

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
     * 数据源连接ID
     */
    private Long dataSourceId;

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
     * 用户id
     */
    private Long userId;

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
