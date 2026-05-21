package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.Environment;
import ai.chat2db.server.domain.api.param.EnvironmentPageQueryParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;

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
    List<Environment> listQuery(List<Long> idList);

    /**
     * Paging Query Data
     *
     * @param param
     * @return
     */
    ServicePage<Environment> pageQuery(EnvironmentPageQueryParam param);

}
