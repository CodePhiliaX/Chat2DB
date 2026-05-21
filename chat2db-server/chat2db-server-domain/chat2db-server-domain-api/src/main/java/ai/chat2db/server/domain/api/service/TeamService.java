package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.param.team.TeamCreateParam;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.team.TeamSelector;
import ai.chat2db.server.domain.api.param.team.TeamUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import jakarta.validation.constraints.NotNull;

/**
 * team
 *
 * @author Jiaju Zhuang
 */
public interface TeamService {

    /**
     * Pagination query
     *
     * @param param
     * @param selector
     * @return
     */
    ServicePage<Team> pageQuery(TeamPageQueryParam param, TeamSelector selector);

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    List<Team> listQuery(List<Long> idList);

    /**
     * Create
     *
     * @param param
     * @return
     */
    Long create(TeamCreateParam param);

    /**
     * update
     *
     * @param param
     * @return
     */
    Long update(TeamUpdateParam param);

    /**
     * delete
     *
     * @param id
     * @return
     */
    void delete(@NotNull Long id);

}
