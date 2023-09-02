package ai.chat2db.server.domain.api.param.team.user;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Team User
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUserPageQueryParam extends PageQueryParam {

    /**
     * 团队id
     */
    @NotNull
    private Long teamId;

    /**
     * 用户id
     */
    @NotNull
    private Long userId;

}
