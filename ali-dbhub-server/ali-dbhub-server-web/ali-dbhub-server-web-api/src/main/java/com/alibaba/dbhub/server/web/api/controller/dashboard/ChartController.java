package com.alibaba.dbhub.server.web.api.controller.dashboard;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.Chart;
import com.alibaba.dbhub.server.domain.api.param.ChartCreateParam;
import com.alibaba.dbhub.server.domain.api.param.ChartUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.ChartService;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.controller.dashboard.converter.ChartWebConverter;
import com.alibaba.dbhub.server.web.api.controller.dashboard.request.ChartCreateRequest;
import com.alibaba.dbhub.server.web.api.controller.dashboard.request.ChartQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.dashboard.request.ChartUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.dashboard.vo.ChartVO;

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
@BusinessExceptionAspect
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
        Chart chart = chartService.find(id).getData();
        ChartVO chartVO = chartWebConverter.model2vo(chart);
        return DataResult.of(chartVO);
    }

    /**
     * 根据ID列表查询报表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/listByIds")
    public ListResult<ChartVO> list(ChartQueryRequest request) {
        List<Chart> charts = chartService.queryByIds(request.getIds()).getData();
        List<ChartVO> chartVOS = chartWebConverter.model2vo(charts);
        return ListResult.of(chartVOS);
    }

    /**
     * 保存图表
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody ChartCreateRequest request) {
        ChartCreateParam chartCreateParam = chartWebConverter.req2param(request);
        return chartService.create(chartCreateParam);
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
        return chartService.update(param);
    }

    /**
     * 删除图表
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return chartService.delete(id);
    }

}
