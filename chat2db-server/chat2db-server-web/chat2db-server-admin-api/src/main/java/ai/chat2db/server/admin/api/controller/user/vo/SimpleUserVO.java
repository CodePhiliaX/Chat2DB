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
}
