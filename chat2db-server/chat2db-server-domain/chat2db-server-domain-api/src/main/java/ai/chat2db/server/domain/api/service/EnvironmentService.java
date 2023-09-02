package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.Environment;
import ai.chat2db.server.domain.api.param.EnvironmentPageQueryParam;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * environment
 *
 * @author Jiaju Zhuang
 */
public interface EnvironmentService {

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    ListResult<Environment> listQuery(List<Long> idList);

    /**
     * Paging Query Data
     *
     * @param param
     * @return
     */
    PageResult<Environment> pageQuery(EnvironmentPageQueryParam param);

}
