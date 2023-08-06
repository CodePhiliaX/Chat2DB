package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;

/**
 * team
 *
 * @author Jiaju Zhuang
 */
public interface TeamService {

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    ListResult<Team> listQuery(List<Long> idList);

}
