package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import jakarta.validation.constraints.NotNull;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
public interface DataSourceAccessBusinessService {
    /**
     * delete
     *
     * @param dataSourceId
     * @return
     */
    ActionResult checkPermission(@NotNull Long dataSourceId);
}
