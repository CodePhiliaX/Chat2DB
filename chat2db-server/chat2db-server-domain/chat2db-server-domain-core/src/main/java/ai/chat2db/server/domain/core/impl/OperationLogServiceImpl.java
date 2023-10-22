package ai.chat2db.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.model.OperationLog;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.domain.api.param.operation.OperationLogPageQueryParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.OperationLogService;
import ai.chat2db.server.domain.core.converter.OperationLogConverter;
import ai.chat2db.server.domain.repository.entity.OperationLogDO;
import ai.chat2db.server.domain.repository.mapper.OperationLogMapper;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.model.EasyLambdaQueryWrapper;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.EasySqlUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version UserExecutedDdlCoreServiceImpl.java, v 0.1 2022年09月25日 14:07 moji Exp $
 * @date 2022/09/25
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private OperationLogConverter operationLogConverter;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public DataResult<Long> create(OperationLogCreateParam param) {
        OperationLogDO userExecutedDdlDO = operationLogConverter.param2do(param);
        userExecutedDdlDO.setGmtCreate(LocalDateTime.now());
        userExecutedDdlDO.setGmtModified(LocalDateTime.now());
        userExecutedDdlDO.setUserId(ContextUtils.getUserId());
        operationLogMapper.insert(userExecutedDdlDO);
        return DataResult.of(userExecutedDdlDO.getId());
    }

    @Override
    public PageResult<OperationLog> queryPage(OperationLogPageQueryParam param) {
        EasyLambdaQueryWrapper<OperationLogDO> queryWrapper = new EasyLambdaQueryWrapper<>();
        queryWrapper.likeWhenPresent(OperationLogDO::getDdl, EasySqlUtils.buildLikeRightFuzzy(param.getSearchKey()))
                .eqWhenPresent(OperationLogDO::getUserId, param.getUserId())
                .eqWhenPresent(OperationLogDO::getDataSourceId, param.getDataSourceId())
                .eqWhenPresent(OperationLogDO::getDatabaseName, param.getDatabaseName())
                .eqWhenPresent(OperationLogDO::getSchemaName, param.getSchemaName())
        ;
        Integer start = param.getPageNo();
        Integer offset = param.getPageSize();
        Page<OperationLogDO> page = new Page<>(start, offset);
        page.setOptimizeCountSql(false);
        page.setOrders(Arrays.asList(OrderItem.desc("gmt_create")));
        IPage<OperationLogDO> executedDdlDOIPage = operationLogMapper.selectPage(page, queryWrapper);
        List<OperationLog> executedDdlDTOS = operationLogConverter.do2dto(executedDdlDOIPage.getRecords());
        if (CollectionUtils.isEmpty(executedDdlDTOS)) {
            return PageResult.empty(param.getPageNo(), param.getPageSize());
        }
        List<Long> dataSourceIds = executedDdlDTOS.stream().map(OperationLog::getDataSourceId).toList();
        ListResult<DataSource> dataSourceListResult = dataSourceService.queryByIds(dataSourceIds);
        Map<Long, DataSource> dataSourceMap = dataSourceListResult.getData().stream().collect(
            Collectors.toMap(DataSource::getId, Function.identity(), (a, b) -> a));
        executedDdlDTOS.stream().forEach(executeDdl -> {
            if (dataSourceMap.containsKey(executeDdl.getDataSourceId())) {
                executeDdl.setDataSourceName(dataSourceMap.get(executeDdl.getDataSourceId()).getAlias());
            }
        });
        return PageResult.of(executedDdlDTOS, executedDdlDOIPage.getTotal(), param);
    }
}
