package ai.chat2db.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.OperationService;
import ai.chat2db.server.domain.core.converter.OperationConverter;
import ai.chat2db.server.domain.core.util.PermissionUtils;
import ai.chat2db.server.domain.repository.entity.OperationSavedDO;
import ai.chat2db.server.domain.repository.mapper.OperationSavedMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.exception.DataNotFoundException;
import ai.chat2db.server.tools.common.model.EasyLambdaQueryWrapper;
import ai.chat2db.server.tools.common.util.ContextUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version UserSavedDdlCoreServiceImpl.java, v 0.1 2022年09月25日 15:50 moji Exp $
 * @date 2022/09/25
 */
@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private OperationSavedMapper operationSavedMapper;

    @Autowired
    private OperationConverter operationConverter;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public DataResult<Long> createWithPermission(OperationSavedParam param) {
        OperationSavedDO userSavedDdlDO = operationConverter.param2do(param);
        userSavedDdlDO.setGmtCreate(LocalDateTime.now());
        userSavedDdlDO.setGmtModified(LocalDateTime.now());
        userSavedDdlDO.setUserId(ContextUtils.getUserId());
        operationSavedMapper.insert(userSavedDdlDO);
        return DataResult.of(userSavedDdlDO.getId());
    }

    @Override
    public ActionResult updateWithPermission(OperationUpdateParam param) {
        Operation data = queryExistent(param.getId()).getData();
        PermissionUtils.checkOperationPermission(data.getUserId());

        OperationSavedDO userSavedDdlDO = operationConverter.param2do(param);
        userSavedDdlDO.setGmtModified(LocalDateTime.now());
        operationSavedMapper.updateById(userSavedDdlDO);
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<Operation> find(Long id) {
        OperationSavedDO operationSavedDO = operationSavedMapper.selectById(id);
        List<Long> dataSourceIds = Lists.newArrayList(operationSavedDO.getDataSourceId());
        Map<Long, DataSource> dataSourceMap = getDataSourceInfo(dataSourceIds);
        Operation operation = operationConverter.do2dto(operationSavedDO);
        operation.setDataSourceName(dataSourceMap.containsKey(operation.getDataSourceId()) ? dataSourceMap.get(
            operation.getDataSourceId()).getAlias() : null);
        return DataResult.of(operation);
    }

    @Override
    public DataResult<Operation> queryExistent(Long id) {
        DataResult<Operation> dataResult = find(id);
        if (dataResult.getData() == null) {
            throw new DataNotFoundException();
        }
        return dataResult;
    }

    @Override
    public DataResult<Operation> queryExistent(OperationQueryParam param) {
        EasyLambdaQueryWrapper<OperationSavedDO> queryWrapper = new EasyLambdaQueryWrapper<>();
        queryWrapper.eqWhenPresent(OperationSavedDO::getId, param.getId())
            .eqWhenPresent(OperationSavedDO::getUserId, param.getUserId());
        IPage<OperationSavedDO> page = operationSavedMapper.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            throw new DataNotFoundException();
        }
        return DataResult.of(operationConverter.do2dto(page.getRecords().get(0)));
    }

    @Override
    public ActionResult deleteWithPermission(Long id) {
        Operation data = queryExistent(id).getData();
        PermissionUtils.checkOperationPermission(data.getUserId());

        operationSavedMapper.deleteById(id);
        return ActionResult.isSuccess();
    }

    @Override
    public PageResult<Operation> queryPage(OperationPageQueryParam param) {
        QueryWrapper<OperationSavedDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(param.getSearchKey())) {
            queryWrapper.like("name", param.getSearchKey());
        }
        if (Objects.nonNull(param.getDataSourceId())) {
            queryWrapper.eq("data_source_id", param.getDataSourceId());
        }
        if (StringUtils.isNotBlank(param.getDatabaseName())) {
            queryWrapper.eq("database_name", param.getDatabaseName());
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (StringUtils.isNotBlank(param.getTabOpened())) {
            queryWrapper.eq("tab_opened", param.getTabOpened());
        }
        if (StringUtils.isNotBlank(param.getOperationType())) {
            queryWrapper.eq("operation_type", param.getOperationType());
        }
        if (param.getUserId() != null) {
            queryWrapper.eq("user_id", param.getUserId());
        }
        Integer start = param.getPageNo();
        Integer offset = param.getPageSize();
        Page<OperationSavedDO> page = new Page<>(start, offset);
        page.setOptimizeCountSql(false);
        if (param.isOrderByDesc()) {
            queryWrapper.orderByDesc("gmt_modified");
        } else {
            queryWrapper.orderByAsc("gmt_modified");
        }
        IPage<OperationSavedDO> iPage = operationSavedMapper.selectPage(page, queryWrapper);
        List<Operation> userSavedDdlDOS = operationConverter.do2dto(iPage.getRecords());
        if (CollectionUtils.isEmpty(userSavedDdlDOS)) {
            return PageResult.empty(param.getPageNo(), param.getPageSize());
        }
        List<Long> dataSourceIds = userSavedDdlDOS.stream().map(Operation::getDataSourceId).toList();
        Map<Long, DataSource> dataSourceMap = getDataSourceInfo(dataSourceIds);
        userSavedDdlDOS.forEach(userSavedDdl -> userSavedDdl.setDataSourceName(
            dataSourceMap.containsKey(userSavedDdl.getDataSourceId()) ? dataSourceMap.get(
                userSavedDdl.getDataSourceId()).getAlias() : null));
        return PageResult.of(userSavedDdlDOS, iPage.getTotal(), param);
    }

    /**
     * 查询数据源信息
     *
     * @param dataSourceIds
     * @return
     */
    private Map<Long, DataSource> getDataSourceInfo(List<Long> dataSourceIds) {
        if (CollectionUtils.isEmpty(dataSourceIds)) {
            return Maps.newHashMap();
        }
        ListResult<DataSource> dataSourceListResult = dataSourceService.queryByIds(dataSourceIds);
        Map<Long, DataSource> dataSourceMap = dataSourceListResult.getData().stream().collect(
            Collectors.toMap(DataSource::getId, Function.identity(), (a, b) -> a));
        return dataSourceMap;
    }
}

