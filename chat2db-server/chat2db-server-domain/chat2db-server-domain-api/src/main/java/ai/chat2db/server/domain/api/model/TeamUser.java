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
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * team id
     */
    @NotNull
    private Long teamId;

    /**
     * team
     */
    @NotNull
    private Team team;

    /**
     * user id
     */
    @NotNull
    private Long userId;

    /**
     * user
     */
    @NotNull
    private User user;

}
