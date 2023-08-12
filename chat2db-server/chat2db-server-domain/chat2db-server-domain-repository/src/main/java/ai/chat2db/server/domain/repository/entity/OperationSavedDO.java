package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 我的保存表
 * </p>
 *
 * @author ali-dbhub
 * @since 2023-04-22
 */
@Getter
@Setter
@TableName("OPERATION_SAVED")
public class OperationSavedDO implements Serializable {

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
     * 用户id
     */
    private Long userId;

    /**
     * schema名称
     */
    private String dbSchemaName;

    /**
     * operation type
     */
    private String operationType;
}
