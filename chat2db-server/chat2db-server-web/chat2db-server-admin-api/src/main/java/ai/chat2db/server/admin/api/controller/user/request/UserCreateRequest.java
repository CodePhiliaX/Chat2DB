package ai.chat2db.server.admin.api.controller.user.request;

import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * create
 *@author Jiaju Zhuang
 */
@Data
public class UserCreateRequest {
    /**
     * userName
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
    @NotNull
    private String roleCode;

    /**
     * user status
     *
     * @see ValidStatusEnum
     */
    @NotNull
    private String status;
}
