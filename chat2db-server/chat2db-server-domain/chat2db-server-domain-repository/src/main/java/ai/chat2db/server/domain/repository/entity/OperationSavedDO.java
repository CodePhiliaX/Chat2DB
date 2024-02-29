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
 * My save list
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
     * save name
     */
    private String name;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl statement status: DRAFT/RELEASE
     */
    private String status;

    /**
     * ddl content
     */
    private String ddl;

    /**
     * Whether it is opened in the tab, y means open, n means not opened
     */
    private String tabOpened;

    /**
     * user id
     */
    private Long userId;

    /**
     * schema name
     */
    private String dbSchemaName;

    /**
     * operation type
     */
    private String operationType;
}
