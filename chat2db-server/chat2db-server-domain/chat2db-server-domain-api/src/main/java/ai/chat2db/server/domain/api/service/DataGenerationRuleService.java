package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.ColumnConfigParam;

import java.util.List;

public interface DataGenerationRuleService {

    List<ColumnConfigParam> getColumnConfigs(Long dataSourceId, String databaseName, String schemaName, String tableName);

    void saveColumnConfigs(Long dataSourceId, String databaseName, String schemaName, String tableName, Long userId, List<ColumnConfigParam> configs, Integer rowCount);
}
