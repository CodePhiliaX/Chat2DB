package ai.chat2db.server.admin.api.controller.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * update
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUpdateRequest {
    /**
     * primary key
     */
    @NotNull
    private Long id;

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
