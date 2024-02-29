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
 * My execution record table
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
     * primary key
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * creation time
     */
    private LocalDateTime gmtCreate;

    /**
     * modified time
     */
    private LocalDateTime gmtModified;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * db name
     */
    private String databaseName;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * user id
     */
    private Long userId;

    /**
     * status
     */
    private String status;

    /**
     * Number of operation lines
     */
    private Long operationRows;

    /**
     * Length of use
     */
    private Long useTime;

    /**
     * Extended Information
     */
    private String extendInfo;

    /**
     * schema name
     */
    private String schemaName;
}
