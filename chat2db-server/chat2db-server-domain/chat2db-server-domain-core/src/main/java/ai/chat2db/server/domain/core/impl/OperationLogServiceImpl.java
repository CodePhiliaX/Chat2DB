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
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.domain.repository.entity.OperationLogDO;
import ai.chat2db.server.domain.repository.mapper.OperationLogMapper;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import ai.chat2db.server.tools.common.model.EasyLambdaQueryWrapper;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.EasySqlUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    private OperationLogMapper getMapper() {
        return Dbutils.getMapper(OperationLogMapper.class);
    }

    @Autowired
    private OperationLogConverter operationLogConverter;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public Long create(OperationLogCreateParam param) {
        OperationLogDO userExecutedDdlDO = operationLogConverter.param2do(param);
        userExecutedDdlDO.setGmtCreate(LocalDateTime.now());
        userExecutedDdlDO.setGmtModified(LocalDateTime.now());
        userExecutedDdlDO.setUserId(ContextUtils.getUserId());
        getMapper().insert(userExecutedDdlDO);
        return userExecutedDdlDO.getId();
    }

    @Override
    public ServicePage<OperationLog> queryPage(OperationLogPageQueryParam param) {
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
        IPage<OperationLogDO> executedDdlDOIPage = getMapper().selectPage(page, queryWrapper);
        List<OperationLog> executedDdlDTOS = operationLogConverter.do2dto(executedDdlDOIPage.getRecords());
        if (CollectionUtils.isEmpty(executedDdlDTOS)) {
            return ServicePage.empty(param.getPageNo(), param.getPageSize());
        }
        List<Long> dataSourceIds = executedDdlDTOS.stream().map(OperationLog::getDataSourceId).toList();
        List<DataSource> dataSources = dataSourceService.listQuery(dataSourceIds, null);
        Map<Long, DataSource> dataSourceMap = dataSources.stream().collect(
            Collectors.toMap(DataSource::getId, Function.identity(), (a, b) -> a));
        executedDdlDTOS.stream().forEach(executeDdl -> {
            if (dataSourceMap.containsKey(executeDdl.getDataSourceId())) {
                executeDdl.setDataSourceName(dataSourceMap.get(executeDdl.getDataSourceId()).getAlias());
            }
        });
        return ServicePage.of(executedDdlDTOS, executedDdlDOIPage.getTotal(), param.getPageNo(), param.getPageSize());
    }
}
