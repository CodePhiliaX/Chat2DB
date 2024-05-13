package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.Dashboard;
import ai.chat2db.server.domain.api.param.dashboard.DashboardCreateParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardPageQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import jakarta.validation.constraints.NotNull;

/**
 * @author moji
 * @version DashboardService.java, v 0.1 June 9, 2023 15:28 moji Exp $
 * @date 2023/06/09
 */
public interface DashboardService {

    /**
     * Save report
     *
     * @param param
     * @return
     */
    DataResult<Long> createWithPermission(DashboardCreateParam param);

    /**
     * Update report
     *
     * @param param
     * @return
     */
    ActionResult updateWithPermission(DashboardUpdateParam param);

    /**
     * Query based on id
     *
     * @param id
     * @return
     */
    DataResult<Dashboard> find(@NotNull Long id);

    /**
     * Query a piece of data
     *
     * @param param
     * @param selector
     * @return
     */
    DataResult<Dashboard> queryExistent(@NotNull DashboardQueryParam param);

    /**
     * Query a piece of data
     *
     * @param id
     * @return
     */
    DataResult<Dashboard> queryExistent(@NotNull Long id);

    /**
     * delete
     *
     * @param id
     * @return
     */
    ActionResult deleteWithPermission(@NotNull Long id);

    /**
     * Query report list
     *
     * @param param
     * @return
     */
    PageResult<Dashboard> queryPage(DashboardPageQueryParam param);
}
