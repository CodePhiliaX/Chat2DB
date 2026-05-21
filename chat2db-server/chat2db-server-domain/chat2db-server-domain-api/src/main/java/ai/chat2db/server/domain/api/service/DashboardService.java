package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.Dashboard;
import ai.chat2db.server.domain.api.param.dashboard.DashboardCreateParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardPageQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import jakarta.validation.constraints.NotNull;

/**
 * @author moji
 * @version DashboardService.java, v 0.1 2023年06月09日 15:28 moji Exp $
 * @date 2023/06/09
 */
public interface DashboardService {

    /**
     * 保存报表
     *
     * @param param
     * @return
     */
    Long createWithPermission(DashboardCreateParam param);

    /**
     * 更新报表
     *
     * @param param
     * @return
     */
    void updateWithPermission(DashboardUpdateParam param);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    Dashboard find(@NotNull Long id);

    /**
     * 查询一条数据
     *
     * @param param
     * @param selector
     * @return
     */
    Dashboard queryExistent(@NotNull DashboardQueryParam param);

    /**
     * 查询一条数据
     *
     * @param id
     * @return
     */
    Dashboard queryExistent(@NotNull Long id);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    void deleteWithPermission(@NotNull Long id);

    /**
     * 查询报表列表
     *
     * @param param
     * @return
     */
    ServicePage<Dashboard> queryPage(DashboardPageQueryParam param);
}
