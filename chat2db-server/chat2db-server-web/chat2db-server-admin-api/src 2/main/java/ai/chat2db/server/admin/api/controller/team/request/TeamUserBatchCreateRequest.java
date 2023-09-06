package ai.chat2db.server.admin.api.controller.team.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUserBatchCreateRequest {

    /**
     * team id
     */
    @NotNull
    private Long teamId;

    /**
     * user id list
     */
    @NotNull
    private List<Long> userIdList;
}
