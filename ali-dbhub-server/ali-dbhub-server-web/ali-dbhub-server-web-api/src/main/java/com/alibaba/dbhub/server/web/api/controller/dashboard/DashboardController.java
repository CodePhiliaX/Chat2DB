package com.alibaba.dbhub.server.web.api.controller.dashboard;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.Dashboard;
import com.alibaba.dbhub.server.domain.api.param.DashboardCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardPageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.DashboardService;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.web.WebPageResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.controller.dashboard.converter.DashboardWebConverter;
import com.alibaba.dbhub.server.web.api.controller.dashboard.request.DashboardCreateRequest;
import com.alibaba.dbhub.server.web.api.controller.dashboard.request.DashboardUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.dashboard.vo.DashboardVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 保存报表类
 *
 * @author moji
 * @version DashboardController.java, v 0.1 2022年09月18日 10:55 moji Exp $
 * @date 2022/09/18
 */
@BusinessExceptionAspect
@RequestMapping("/api/dashboard")
@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DashboardWebConverter dashboardWebConverter;

    /**
     * 查询报表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public WebPageResult<DashboardVO> list(DashboardPageQueryParam request) {
        PageResult<Dashboard> result = dashboardService.queryPage(request);
        List<DashboardVO> dashboardVOS = dashboardWebConverter.model2vo(result.getData());
        return WebPageResult.of(dashboardVOS, result.getTotal(), result.getPageNo(), result.getPageSize());
    }

    /**
     * 根据id查询报表详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public DataResult<DashboardVO> get(@PathVariable("id") Long id) {
        Dashboard dashboard = dashboardService.find(id).getData();
        DashboardVO dashboardVO = dashboardWebConverter.model2vo(dashboard);
        return DataResult.of(dashboardVO);
    }

    /**
     * 保存报表
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody DashboardCreateRequest request) {
        DashboardCreateParam param = dashboardWebConverter.req2param(request);
        return dashboardService.create(param);
    }

    /**
     * 更新报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody DashboardUpdateRequest request) {
        DashboardUpdateParam param = dashboardWebConverter.req2updateParam(request);
        return dashboardService.update(param);
    }

    /**
     * 删除报表
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return dashboardService.delete(id);
    }
}
