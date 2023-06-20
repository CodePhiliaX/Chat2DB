package com.alibaba.dbhub.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.alibaba.dbhub.server.domain.api.model.Dashboard;
import com.alibaba.dbhub.server.domain.api.param.DashboardCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardPageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DashboardUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.DashboardService;
import com.alibaba.dbhub.server.domain.core.converter.DashboardConverter;
import com.alibaba.dbhub.server.domain.repository.entity.DashboardChartRelationDO;
import com.alibaba.dbhub.server.domain.repository.entity.DashboardDO;
import com.alibaba.dbhub.server.domain.repository.mapper.DashboardChartRelationMapper;
import com.alibaba.dbhub.server.domain.repository.mapper.DashboardMapper;
import com.alibaba.dbhub.server.tools.base.enums.YesOrNoEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DashboardServiceImpl.java, v 0.1 2023年06月09日 16:06 moji Exp $
 * @date 2023/06/09
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private DashboardChartRelationMapper dashboardChartRelationMapper;

    @Autowired
    private DashboardConverter dashboardConverter;

    @Override
    public DataResult<Long> create(DashboardCreateParam param) {
        param.setGmtCreate(LocalDateTime.now());
        param.setGmtModified(LocalDateTime.now());
        DashboardDO dashboardDO = dashboardConverter.param2do(param);
        long id = dashboardMapper.insert(dashboardDO);
        insertDashboardRelation(id, param.getChartIds());
        return DataResult.of(id);
    }

    @Override
    public ActionResult update(DashboardUpdateParam param) {
        param.setGmtModified(LocalDateTime.now());
        DashboardDO dashboardDO = dashboardConverter.updateParam2do(param);
        dashboardMapper.updateById(dashboardDO);
        deleteDashboardRelation(dashboardDO.getId());
        insertDashboardRelation(dashboardDO.getId(), param.getChartIds());
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<Dashboard> find(Long id) {
        DashboardDO dashboardDO = dashboardMapper.selectById(id);
        if (YesOrNoEnum.YES.getLetter().equals(dashboardDO.getDeleted())) {
            return DataResult.empty();
        }
        Dashboard dashboard = dashboardConverter.do2model(dashboardDO);
        return DataResult.of(dashboard);
    }

    @Override
    public ActionResult delete(Long id) {
        DashboardDO dashboardDO = dashboardMapper.selectById(id);
        if (Objects.isNull(dashboardDO)) {
            return ActionResult.isSuccess();
        }
        dashboardDO.setDeleted(YesOrNoEnum.YES.getLetter());
        dashboardMapper.updateById(dashboardDO);
        deleteDashboardRelation(id);
        return ActionResult.isSuccess();
    }

    /**
     * delete dashboard relation
     *
     * @param id
     */
    private void deleteDashboardRelation(Long id) {
        LambdaQueryWrapper<DashboardChartRelationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DashboardChartRelationDO::getDashboardId, id);
        List<DashboardChartRelationDO> relationDO = dashboardChartRelationMapper.selectList(queryWrapper);
        List<Long> relationIds = relationDO.stream().map(DashboardChartRelationDO::getId).toList();
        if (CollectionUtils.isNotEmpty(relationIds)) {
            dashboardChartRelationMapper.deleteBatchIds(relationIds);
        }
    }

    /**
     * insert dashboard relation
     *
     * @param dashboardId
     * @param chartIds
     */
    private void insertDashboardRelation(Long dashboardId, List<Long> chartIds) {
        if (Objects.isNull(dashboardId) || CollectionUtils.isEmpty(chartIds)) {
            return;
        }
        chartIds.forEach(chartId -> {
            DashboardChartRelationDO relationDO = new DashboardChartRelationDO();
            relationDO.setGmtCreate(LocalDateTime.now());
            relationDO.setGmtModified(LocalDateTime.now());
            relationDO.setDashboardId(dashboardId);
            relationDO.setChartId(chartId);
            dashboardChartRelationMapper.insert(relationDO);
        });
    }

    @Override
    public PageResult<Dashboard> queryPage(DashboardPageQueryParam param) {
        LambdaQueryWrapper<DashboardDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getSearchKey())) {
            queryWrapper.like(DashboardDO::getName, param.getSearchKey());
        }
        Integer start = param.getPageNo();
        Integer offset = param.getPageSize();
        Page<DashboardDO> page = new Page<>(start, offset);
        IPage<DashboardDO> iPage = dashboardMapper.selectPage(page, queryWrapper);
        List<Dashboard> dashboards = dashboardConverter.do2model(iPage.getRecords());
        return PageResult.of(dashboards, iPage.getTotal(), param);
    }
}
