package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.DataSource;
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
     * @param dataSource
     * @return
     */
    ActionResult checkPermission(@NotNull DataSource dataSource);
}
