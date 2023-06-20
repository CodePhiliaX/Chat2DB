package com.alibaba.dbhub.server.domain.api.service;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.api.model.Dashboard;
import com.alibaba.dbhub.server.domain.api.param.DashboardPageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardUpdateParam;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;

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
    DataResult<Long> create(DashboardCreateParam param);

    /**
     * 更新报表
     *
     * @param param
     * @return
     */
    ActionResult update(DashboardUpdateParam param);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    DataResult<Dashboard> find(@NotNull Long id);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    ActionResult delete(@NotNull Long id);

    /**
     * 查询报表列表
     *
     * @param param
     * @return
     */
    PageResult<Dashboard> queryPage(DashboardPageQueryParam param);
}
