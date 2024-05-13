package ai.chat2db.server.admin.api.controller.user.vo;

import java.util.Date;

import ai.chat2db.server.common.api.controller.vo.SimpleUserVO;
import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class UserPageQueryVO {
    /**
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * userName
     */
    @NotNull
    private String userName;

    /**
     * Nick name
     */
    @NotNull
    private String nickName;

    /**
     * user status
     *
     * @see ValidStatusEnum
     */
    private String status;

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
     * Change the time
     */
    private Date gmtModified;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;

    /**
     * Modifier user
     */
    private SimpleUserVO modifiedUser;

}
