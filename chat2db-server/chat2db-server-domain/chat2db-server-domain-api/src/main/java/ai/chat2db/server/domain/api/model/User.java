package ai.chat2db.server.domain.api.model;

import java.util.Date;

import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * User Info
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * username
     */
    @NotNull
    private String userName;

    /**
     * password
     */
    @NotNull
    private String password;

    /**
     * Nick name
     */
    @NotNull
    private String nickName;

    /**
     * email
     */
    @NotNull
    private String email;

    /**
     * role coding
     *
     * @see RoleCodeEnum
     */
    private String roleCode;

    /**
     * user status
     *
     * @see ValidStatusEnum
     */
    @NotNull
    private String status;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;

    /**
     * Modifier user
     */
    private User modifiedUser;
}
