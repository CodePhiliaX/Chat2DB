package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.param.team.TeamCreateParam;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.team.TeamSelector;
import ai.chat2db.server.domain.api.param.team.TeamUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
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
    PageResult<Team> pageQuery(TeamPageQueryParam param, TeamSelector selector);

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    ListResult<Team> listQuery(List<Long> idList);

    /**
     * Create
     *
     * @param param
     * @return
     */
    DataResult<Long> create(TeamCreateParam param);

    /**
     * update
     *
     * @param param
     * @return
     */
    DataResult<Long> update(TeamUpdateParam param);

    /**
     * delete
     *
     * @param id
     * @return
     */
    ActionResult delete(@NotNull Long id);

}
