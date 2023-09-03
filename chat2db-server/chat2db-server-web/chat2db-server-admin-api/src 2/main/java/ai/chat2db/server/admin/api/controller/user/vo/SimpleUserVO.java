
package ai.chat2db.server.admin.api.controller.user.vo;

import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * user
 *
 * @author Jiaju Zhuang
 */
@Data
public class SimpleUserVO {
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
}
