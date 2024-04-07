package ai.chat2db.server.domain.api.param.team;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamCreateParam {
    /**
     * team coding
     */
    @NotNull
    private String code;

    /**
     * Team Name
     */
    @NotNull
    private String name;

    /**
     * Team status
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    @NotNull
    private String status;


    /**
     * role coding
     *
     * @see ai.chat2db.server.domain.api.enums.RoleCodeEnum
     */
    @NotNull
    private String roleCode;

    /**
     * Team description
     */
    private String description;
}
