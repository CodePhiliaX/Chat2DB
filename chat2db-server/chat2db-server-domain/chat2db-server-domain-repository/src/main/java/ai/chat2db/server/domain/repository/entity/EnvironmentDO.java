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
 * Database connection environment
 * </p>
 *
 * @author chat2db
 * @since 2023-09-09
 */
@Getter
@Setter
@TableName("ENVIRONMENT")
public class EnvironmentDO implements Serializable {

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
     * environment name
     */
    private String name;

    /**
     * environment abbreviation
     */
    private String shortName;

    /**
     * color
     */
    private String color;
}
