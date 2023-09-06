package ai.chat2db.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.domain.api.chart.ChartCreateParam;
import ai.chat2db.server.domain.api.chart.ChartListQueryParam;
import ai.chat2db.server.domain.api.chart.ChartQueryParam;
import ai.chat2db.server.domain.api.chart.ChartUpdateParam;
import ai.chat2db.server.domain.api.model.Chart;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.service.ChartService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.core.converter.ChartConverter;
import ai.chat2db.server.domain.core.util.PermissionUtils;
import ai.chat2db.server.domain.repository.entity.ChartDO;
import ai.chat2db.server.domain.repository.entity.DashboardChartRelationDO;
import ai.chat2db.server.domain.repository.mapper.ChartMapper;
import ai.chat2db.server.domain.repository.mapper.DashboardChartRelationMapper;
import ai.chat2db.server.tools.base.enums.YesOrNoEnum;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.exception.DataNotFoundException;
import ai.chat2db.server.tools.common.model.EasyLambdaQueryWrapper;
import ai.chat2db.server.tools.common.util.ContextUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public DataResult<Long> createWithPermission(ChartCreateParam param) {
        param.setGmtCreate(LocalDateTime.now());
        param.setGmtModified(LocalDateTime.now());
        param.setDeleted(YesOrNoEnum.NO.getLetter());
        param.setUserId(ContextUtils.getUserId());
        ChartDO chartDO = chartConverter.param2do(param);
        chartMapper.insert(chartDO);
        return DataResult.of(chartDO.getId());
    }

    @Override
    public ActionResult updateWithPermission(ChartUpdateParam param) {
        Chart data = queryExistent(param.getId()).getData();
        PermissionUtils.checkOperationPermission(data.getUserId());

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
    public DataResult<Chart> queryExistent(ChartQueryParam param) {
        EasyLambdaQueryWrapper<ChartDO> queryWrapper = new EasyLambdaQueryWrapper<>();
        queryWrapper
            .eq(ChartDO::getDeleted, YesOrNoEnum.NO.getLetter())
            .eqWhenPresent(ChartDO::getId, param.getId())
            .eqWhenPresent(ChartDO::getUserId, param.getUserId());
        IPage<ChartDO> page = chartMapper.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            throw new DataNotFoundException();
        }
        Chart data = chartConverter.do2model(page.getRecords().get(0));
        setDataSourceInfo(Lists.newArrayList(data));
        return DataResult.of(data);
    }

    @Override
    public DataResult<Chart> queryExistent(Long id) {
        DataResult<Chart> dataResult = find(id);
        if (dataResult.getData() == null) {
            throw new DataNotFoundException();
        }
        return dataResult;
    }

    @Override
    public ListResult<Chart> listQuery(ChartListQueryParam param) {
        EasyLambdaQueryWrapper<ChartDO> queryWrapper = new EasyLambdaQueryWrapper<>();
        queryWrapper
            .eq(ChartDO::getDeleted, YesOrNoEnum.NO.getLetter())
            .inWhenPresent(ChartDO::getId, param.getIdList())
            .eqWhenPresent(ChartDO::getUserId, param.getUserId());
        List<ChartDO> queryList = chartMapper.selectList(queryWrapper);
        List<Chart> list = chartConverter.do2model(queryList);
        setDataSourceInfo(list);
        return ListResult.of(list);
    }

    @Override
    public ActionResult deleteWithPermission(Long id) {
        Chart data = queryExistent(id).getData();
        PermissionUtils.checkOperationPermission(data.getUserId());

        ChartDO chartDO = new ChartDO();
        chartDO.setId(id);
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
