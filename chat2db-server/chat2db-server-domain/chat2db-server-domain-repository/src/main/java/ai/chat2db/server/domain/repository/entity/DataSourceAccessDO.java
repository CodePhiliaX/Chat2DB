package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * Data source authorization
 * </p>
 *
 * @author chat2db
 * @since 2023-08-26
 */
@Getter
@Setter
@TableName("DATA_SOURCE_ACCESS")
public class DataSourceAccessDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * creation time
     */
    private Date gmtCreate;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Creator user id
     */
    private Long createUserId;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * Authorization type
     */
    private String accessObjectType;

    /**
     * Authorization ID, distinguish whether it is a user or a team according to the type
     */
    private Long accessObjectId;
}
