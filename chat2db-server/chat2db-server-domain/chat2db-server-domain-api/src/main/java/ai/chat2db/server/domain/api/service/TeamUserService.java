package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * team user
 *
 * @author Jiaju Zhuang
 */
public interface TeamUserService {

    /**
     * Comprehensive Paging Query Data
     *
     * @param param
     * @param selector
     * @return
     */
    PageResult<TeamUser> comprehensivePageQuery(TeamUserComprehensivePageQueryParam param, TeamUserSelector selector);

}
