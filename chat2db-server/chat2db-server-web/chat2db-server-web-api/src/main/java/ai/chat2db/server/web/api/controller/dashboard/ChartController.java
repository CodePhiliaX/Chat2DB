package ai.chat2db.server.web.api.controller.dashboard;

import ai.chat2db.server.domain.api.chart.ChartCreateParam;
import ai.chat2db.server.domain.api.chart.ChartListQueryParam;
import ai.chat2db.server.domain.api.chart.ChartQueryParam;
import ai.chat2db.server.domain.api.chart.ChartUpdateParam;
import ai.chat2db.server.domain.api.service.ChartService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.dashboard.converter.ChartWebConverter;
import ai.chat2db.server.web.api.controller.dashboard.request.ChartCreateRequest;
import ai.chat2db.server.web.api.controller.dashboard.request.ChartQueryRequest;
import ai.chat2db.server.web.api.controller.dashboard.request.ChartUpdateRequest;
import ai.chat2db.server.web.api.controller.dashboard.vo.ChartVO;
import jakarta.validation.Valid;
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
 * 保存图表类
 *
 * @author moji
 * @version ChartController.java, v 0.1 2022年09月18日 10:55 moji Exp $
 * @date 2022/09/18
 */
@RequestMapping("/api/chart")
@RestController
public class ChartController {

    @Autowired
    private ChartService chartService;

    @Autowired
    private ChartWebConverter chartWebConverter;

    /**
     * 根据id查询图表详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public DataResult<ChartVO> get(@PathVariable("id") Long id) {
        ChartQueryParam param = new ChartQueryParam();
        param.setId(id);
        param.setUserId(ContextUtils.getUserId());
        return chartService.queryExistent(param)
            .map(chartWebConverter::model2vo);
    }

    /**
     * 根据ID列表查询报表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/listByIds")
    public ListResult<ChartVO> list(ChartQueryRequest request) {
        ChartListQueryParam param = new ChartListQueryParam();
        param.setIdList(request.getIds());
        param.setUserId(ContextUtils.getUserId());
        return chartService.listQuery(param)
            .map(chartWebConverter::model2vo);
    }

    /**
     * 保存图表
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@Valid @RequestBody ChartCreateRequest request) {
        ChartCreateParam chartCreateParam = chartWebConverter.req2param(request);
        return chartService.createWithPermission(chartCreateParam);
    }

    /**
     * 更新图表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody ChartUpdateRequest request) {
        ChartUpdateParam param = chartWebConverter.req2updateParam(request);
        return chartService.updateWithPermission(param);
    }

    /**
     * 删除图表
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return chartService.deleteWithPermission(id);
    }

}
