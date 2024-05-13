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
 * Data source connection table
 * </p>
 *
 * @author chat2db
 * @since 2023-08-26
 */
@Getter
@Setter
@TableName("DBHUB_USER")
public class DbhubUserDO implements Serializable {

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
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * Nick name
     */
    private String nickName;

    /**
     * email
     */
    private String email;

    /**
     * role coding
     */
    private String roleCode;

    /**
     * user status
     */
    private String status;

    /**
     * Creator user id
     */
    private Long createUserId;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;
}
