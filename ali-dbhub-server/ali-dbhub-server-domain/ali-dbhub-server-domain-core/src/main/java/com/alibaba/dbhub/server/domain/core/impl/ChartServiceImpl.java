package com.alibaba.dbhub.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.dbhub.server.domain.api.model.Chart;
import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.ChartCreateParam;
import com.alibaba.dbhub.server.domain.api.param.ChartUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.ChartService;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.core.converter.ChartConverter;
import com.alibaba.dbhub.server.domain.repository.entity.ChartDO;
import com.alibaba.dbhub.server.domain.repository.entity.DashboardChartRelationDO;
import com.alibaba.dbhub.server.domain.repository.mapper.ChartMapper;
import com.alibaba.dbhub.server.domain.repository.mapper.DashboardChartRelationMapper;
import com.alibaba.dbhub.server.tools.base.enums.YesOrNoEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version ChartServiceImpl.java, v 0.1 2023年06月09日 16:06 moji Exp $
 * @date 2023/06/09
 */
@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private ChartMapper chartMapper;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DashboardChartRelationMapper dashboardChartRelationMapper;

    @Autowired
    private ChartConverter chartConverter;

    @Override
    public DataResult<Long> create(ChartCreateParam param) {
        param.setGmtCreate(LocalDateTime.now());
        param.setGmtModified(LocalDateTime.now());
        param.setDeleted(YesOrNoEnum.NO.getLetter());
        ChartDO chartDO = chartConverter.param2do(param);
        long id = chartMapper.insert(chartDO);
        return DataResult.of(id);
    }

    @Override
    public ActionResult update(ChartUpdateParam param) {
        param.setGmtModified(LocalDateTime.now());
        ChartDO chartDO = chartConverter.updateParam2do(param);
        chartMapper.updateById(chartDO);
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<Chart> find(Long id) {
        ChartDO chartDO = chartMapper.selectById(id);
        if (YesOrNoEnum.YES.getLetter().equals(chartDO.getDeleted())) {
            return DataResult.empty();
        }
        Chart chart = chartConverter.do2model(chartDO);
        setDataSourceInfo(Lists.newArrayList(chart));
        return DataResult.of(chart);
    }

    @Override
    public ActionResult delete(Long id) {
        ChartDO chartDO = chartMapper.selectById(id);
        if (Objects.isNull(chartDO)) {
            return ActionResult.isSuccess();
        }
        chartDO.setDeleted(YesOrNoEnum.YES.getLetter());
        chartMapper.updateById(chartDO);
        LambdaQueryWrapper<DashboardChartRelationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DashboardChartRelationDO::getChartId, id);
        List<DashboardChartRelationDO> relationDO = dashboardChartRelationMapper.selectList(queryWrapper);
        List<Long> relationIds = relationDO.stream().map(DashboardChartRelationDO::getId).toList();
        if (CollectionUtils.isNotEmpty(relationIds)) {
            dashboardChartRelationMapper.deleteBatchIds(relationIds);
        }
        return ActionResult.isSuccess();
    }

    @Override
    public ListResult<Chart> queryByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return ListResult.empty();
        }
        List<ChartDO> chartDOS = chartMapper.selectBatchIds(ids);
        List<Chart> charts = chartConverter.do2model(chartDOS);
        List<Chart> result = charts.stream().filter(o -> YesOrNoEnum.NO.getLetter().equals(o.getDeleted())).toList();
        setDataSourceInfo(result);
        return ListResult.of(result);
    }

    /**
     * 回填数据源信息
     *
     * @param result
     */
    private void setDataSourceInfo(List<Chart> result) {
        List<Long> dataSourceIds = result.stream().map(Chart::getDataSourceId).toList();
        ListResult<DataSource> dataSourceListResult = dataSourceService.queryByIds(dataSourceIds);
        Map<Long, DataSource> dataSourceMap = dataSourceListResult.getData().stream().collect(
            Collectors.toMap(DataSource::getId, Function.identity(), (a, b) -> a));
        result.forEach(o -> {
            if (dataSourceMap.containsKey(o.getDataSourceId())) {
                o.setDataSourceName(dataSourceMap.get(o.getDataSourceId()).getAlias());
            }
        });
    }
}
