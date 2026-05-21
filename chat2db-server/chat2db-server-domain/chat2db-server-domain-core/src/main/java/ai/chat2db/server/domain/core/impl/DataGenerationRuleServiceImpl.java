package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.param.ColumnConfigParam;
import ai.chat2db.server.domain.api.service.DataGenerationRuleService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.domain.repository.entity.DataGenerationRuleDO;
import ai.chat2db.server.domain.repository.mapper.DataGenerationRuleMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DataGenerationRuleServiceImpl implements DataGenerationRuleService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private DataGenerationRuleMapper getMapper() {
        return Dbutils.getMapper(DataGenerationRuleMapper.class);
    }

    @Override
    public List<ColumnConfigParam> getColumnConfigs(Long dataSourceId, String databaseName, String schemaName, String tableName) {
        try {
            QueryWrapper<DataGenerationRuleDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_source_id", dataSourceId);
            queryWrapper.eq("database_name", databaseName);
            queryWrapper.eq("table_name", tableName);
            if (schemaName != null) {
                queryWrapper.eq("schema_name", schemaName);
            } else {
                queryWrapper.isNull("schema_name");
            }

            DataGenerationRuleDO rule = getMapper().selectOne(queryWrapper);
            if (rule == null || rule.getColumnConfigs() == null) {
                return Collections.emptyList();
            }

            return JSON_MAPPER.readValue(
                    rule.getColumnConfigs(), new TypeReference<List<ColumnConfigParam>>() {});
        } catch (Exception e) {
            log.error("Failed to get column configs", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void saveColumnConfigs(Long dataSourceId, String databaseName, String schemaName, String tableName, Long userId, List<ColumnConfigParam> configs, Integer rowCount) {
        if (configs == null || configs.isEmpty()) {
            return;
        }
        try {
            QueryWrapper<DataGenerationRuleDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_source_id", dataSourceId);
            queryWrapper.eq("database_name", databaseName);
            queryWrapper.eq("table_name", tableName);
            if (schemaName != null) {
                queryWrapper.eq("schema_name", schemaName);
            } else {
                queryWrapper.isNull("schema_name");
            }

            DataGenerationRuleDO existing = getMapper().selectOne(queryWrapper);
            LocalDateTime now = LocalDateTime.now();
            String jsonConfigs;
            try {
                jsonConfigs = JSON_MAPPER.writeValueAsString(configs);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize column configs", e);
                return;
            }

            if (existing != null) {
                existing.setColumnConfigs(jsonConfigs);
                if (rowCount != null) {
                    existing.setRowCount(rowCount);
                }
                existing.setGmtModified(now);
                getMapper().updateById(existing);
            } else {
                DataGenerationRuleDO rule = new DataGenerationRuleDO();
                rule.setDataSourceId(dataSourceId);
                rule.setDatabaseName(databaseName);
                rule.setSchemaName(schemaName);
                rule.setTableName(tableName);
                rule.setRowCount(rowCount != null ? rowCount : 100);
                rule.setColumnConfigs(jsonConfigs);
                rule.setUserId(userId);
                rule.setGmtCreate(now);
                rule.setGmtModified(now);
                getMapper().insert(rule);
            }
            
        } catch (Exception e) {
            log.error("Failed to save column configs", e);
        }
    }
}
