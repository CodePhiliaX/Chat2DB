
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
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 用户名
     */
    @NotNull
    private String userName;

    /**
     * 昵称
     */
    @NotNull
    private String nickName;

    /**
     * 用户状态
     *
     * @see ValidStatusEnum
     */
    private String status;

    /**
     * 邮箱
     */
    @NotNull
    private String email;

    /**
     * 角色编码
     *
     * @see RoleCodeEnum
     */
    private String roleCode;


    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 修改人用户
     */
    private SimpleUserVO modifiedUser;

}
