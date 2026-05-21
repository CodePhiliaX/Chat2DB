package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.param.DeprecatedTableParam;
import ai.chat2db.server.domain.api.service.DeprecatedTableService;
import ai.chat2db.server.domain.core.converter.DeprecatedTableConverter;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.domain.repository.entity.DeprecatedTableDO;
import ai.chat2db.server.domain.repository.mapper.DeprecatedTableMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeprecatedTableServiceImpl implements DeprecatedTableService {

    @Autowired
    private DeprecatedTableConverter deprecatedTableConverter;

    private DeprecatedTableMapper getMapper() {
        return Dbutils.getMapper(DeprecatedTableMapper.class);
    }

    @Override
    public ActionResult deprecatedTable(DeprecatedTableParam param) {
        DeprecatedTableDO entity = deprecatedTableConverter.param2do(param);
        entity.setUserId(ContextUtils.getUserId());
        getMapper().insert(entity);
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult deleteDeprecatedTable(DeprecatedTableParam param) {
        param.setUserId(ContextUtils.getUserId());
        LambdaUpdateWrapper<DeprecatedTableDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DeprecatedTableDO::getUserId, param.getUserId());
        updateWrapper.eq(DeprecatedTableDO::getDataSourceId, param.getDataSourceId());
        if (StringUtils.isNotBlank(param.getDatabaseName())) {
            updateWrapper.eq(DeprecatedTableDO::getDatabaseName, param.getDatabaseName());
        }
        if (StringUtils.isNotBlank(param.getSchemaName())) {
            updateWrapper.eq(DeprecatedTableDO::getSchemaName, param.getSchemaName());
        }
        if (StringUtils.isNotBlank(param.getTableName())) {
            updateWrapper.eq(DeprecatedTableDO::getTableName, param.getTableName());
        }
        getMapper().delete(updateWrapper);
        return ActionResult.isSuccess();
    }

    @Override
    public List<String> queryDeprecatedTables(DeprecatedTableParam param) {
        List<String> result = new ArrayList<>();
        LambdaQueryWrapper<DeprecatedTableDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeprecatedTableDO::getUserId, param.getUserId());
        queryWrapper.eq(DeprecatedTableDO::getDataSourceId, param.getDataSourceId());
        if (StringUtils.isNotBlank(param.getDatabaseName())) {
            queryWrapper.eq(DeprecatedTableDO::getDatabaseName, param.getDatabaseName());
        }
        if (StringUtils.isNotBlank(param.getSchemaName())) {
            queryWrapper.eq(DeprecatedTableDO::getSchemaName, param.getSchemaName());
        }
        if (StringUtils.isNotBlank(param.getTableName())) {
            queryWrapper.eq(DeprecatedTableDO::getTableName, param.getTableName());
        }
        queryWrapper.orderByDesc(DeprecatedTableDO::getGmtModified);
        List<DeprecatedTableDO> list = getMapper().selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            result = list.stream().map(deprecatedTableDO -> deprecatedTableDO.getTableName()).collect(Collectors.toList());
        }
        return result;
    }
}
