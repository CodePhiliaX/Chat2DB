package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserCreatParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserPageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import jakarta.validation.constraints.NotNull;

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
    ServicePage<TeamUser> pageQuery(TeamUserPageQueryParam param, TeamUserSelector selector);

    /**
     * Comprehensive Paging Query Data
     *
     * @param param
     * @param selector
     * @return
     */
    ServicePage<TeamUser> comprehensivePageQuery(TeamUserComprehensivePageQueryParam param, TeamUserSelector selector);

    /**
     * Create
     *
     * @param param
     * @return
     */
    Long create(TeamUserCreatParam param);

    /**
     * delete
     *
     * @param id
     * @return
     */
    void delete(@NotNull Long id);
}
