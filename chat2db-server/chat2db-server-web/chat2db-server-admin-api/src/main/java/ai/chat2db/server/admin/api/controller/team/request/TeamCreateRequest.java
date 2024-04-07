package ai.chat2db.server.admin.api.controller.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamCreateRequest {

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
     * Team description
     */
    private String description;
}
