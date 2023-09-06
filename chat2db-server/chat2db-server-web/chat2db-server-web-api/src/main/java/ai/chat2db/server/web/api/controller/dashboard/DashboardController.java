package ai.chat2db.server.web.api.controller.dashboard;

import java.util.List;

import ai.chat2db.server.domain.api.model.Dashboard;
import ai.chat2db.server.domain.api.param.dashboard.DashboardCreateParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardPageQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardQueryParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardUpdateParam;
import ai.chat2db.server.domain.api.service.DashboardService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.dashboard.converter.DashboardWebConverter;
import ai.chat2db.server.web.api.controller.dashboard.request.DashboardCreateRequest;
import ai.chat2db.server.web.api.controller.dashboard.request.DashboardUpdateRequest;
import ai.chat2db.server.web.api.controller.dashboard.vo.DashboardVO;

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
        request.setUserId(ContextUtils.getUserId());
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
        DashboardQueryParam param = new DashboardQueryParam();
        param.setId(id);
        param.setUserId(ContextUtils.getUserId());
        return dashboardService.queryExistent(param)
            .map(dashboardWebConverter::model2vo);
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
        return dashboardService.createWithPermission(param);
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
        return dashboardService.updateWithPermission(param);
    }

    /**
     * 删除报表
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return dashboardService.deleteWithPermission(id);
    }
}
