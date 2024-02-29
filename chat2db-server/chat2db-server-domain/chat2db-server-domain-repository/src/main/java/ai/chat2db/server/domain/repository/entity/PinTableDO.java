package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("PIN_TABLE")
public class PinTableDO implements Serializable {

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
    private String schemaName;

    /**
     * userId
     */
    private Long userId;

    /**
     * tableName
     */
    private String tableName;

}
