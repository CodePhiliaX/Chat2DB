package ai.chat2db.server.domain.api.param.team.user;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;

/**
 * Team User
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUserComprehensivePageQueryParam extends PageQueryParam {

    /**
     * team id
     */
    private Long teamId;

    /**
     * user id
     */
    private Long userId;
    

    /**
     * Query keywords for team
     */
    private String teamSearchKey;

    /**
     * Query keywords for user
     */
    private String userSearchKey;
}
