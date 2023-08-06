package ai.chat2db.server.domain.api.model;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Team user
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUser implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 团队id
     */
    @NotNull
    private Long teamId;

    /**
     * 团队
     */
    @NotNull
    private Team team;

    /**
     * 用户id
     */
    @NotNull
    private Long userId;

    /**
     * 用户
     */
    @NotNull
    private User user;

}
