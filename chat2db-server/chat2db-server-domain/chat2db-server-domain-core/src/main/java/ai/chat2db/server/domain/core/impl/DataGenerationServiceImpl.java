package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.param.DataGenerationRequest;
import ai.chat2db.server.domain.api.param.ColumnConfigParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.GeneratorTemplate;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.DataGenerationService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.api.service.DataGenerationRuleService;
import ai.chat2db.server.domain.api.enums.TaskStatusEnum;
import ai.chat2db.server.domain.api.enums.TaskTypeEnum;
import ai.chat2db.server.domain.api.vo.DataGenerationPreviewVO;
import ai.chat2db.server.domain.core.generator.ExpressionDataGenerator;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataGenerationServiceImpl implements DataGenerationService {

    @Autowired
    private TableService tableService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExpressionDataGenerator expressionDataGenerator;

    @Autowired
    private DataGenerationRuleService ruleService;

    @Override
    public List<ColumnConfigParam> getTableColumns(DataGenerationRequest request) {
        try {
            TableQueryParam param = new TableQueryParam();
            param.setDataSourceId(request.getDataSourceId());
            param.setDatabaseName(request.getDatabaseName());
            param.setSchemaName(request.getSchemaName());
            param.setTableName(request.getTableName());

            List<TableColumn> tableColumns = tableService.queryColumns(param);
            if (tableColumns == null) {
                throw new BusinessException("GET_TABLE_COLUMNS_ERROR", new Object[]{"获取表列信息失败"});
            }

            List<ColumnConfigParam> savedConfigs = ruleService.getColumnConfigs(
                    request.getDataSourceId(), request.getDatabaseName(), request.getSchemaName(), request.getTableName());

            Map<String, ColumnConfigParam> savedMap = new HashMap<>();
            if (savedConfigs != null && !savedConfigs.isEmpty()) {
                for (ColumnConfigParam cfg : savedConfigs) {
                    savedMap.put(cfg.getColumnName(), cfg);
                }
            }

            List<ColumnConfigParam> columns = new ArrayList<>();
            for (TableColumn column : tableColumns) {
                ColumnConfigParam config = new ColumnConfigParam();
                config.setColumnName(column.getName());
                String dataType = column.getDataType() != null ? column.getDataType() : "VARCHAR";
                config.setDataType(dataType);
                config.setComment(column.getComment());
                config.setNullable(column.getNullable() != null && column.getNullable() == 1);
                config.setAutoIncrement(column.getAutoIncrement() != null && column.getAutoIncrement());
                config.setMaxLength(column.getColumnSize());
                config.setScale(column.getDecimalDigits());

                ColumnConfigParam saved = savedMap.get(column.getName());
                if (saved != null && saved.getExpression() != null) {
                    config.setExpression(saved.getExpression());
                }

                columns.add(config);
            }

            return columns;
        } catch (Exception e) {
            log.error("Failed to get table columns", e);
            throw new BusinessException("GET_TABLE_COLUMNS_ERROR", new Object[]{"获取表列信息失败: " + e.getMessage()});
        }
    }

    @Override
    public DataGenerationPreviewVO generatePreview(DataGenerationRequest request) {
        try {
            saveConfigs(request);

            List<ColumnConfigParam> columns = resolveColumns(request);
            if (columns == null) {
                throw new BusinessException("GET_TABLE_COLUMNS_ERROR", new Object[]{"获取表列信息失败"});
            }

            List<Map<String, Object>> previewData = generateDataRows(request, columns, 10);

            DataGenerationPreviewVO previewVO = new DataGenerationPreviewVO();
            previewVO.setTableName(request.getTableName());
            previewVO.setPreviewData(previewData);

            List<DataGenerationPreviewVO.ColumnInfo> columnInfos = new ArrayList<>();
            for (ColumnConfigParam column : columns) {
                DataGenerationPreviewVO.ColumnInfo columnInfo = new DataGenerationPreviewVO.ColumnInfo();
                columnInfo.setColumnName(column.getColumnName());
                columnInfo.setDataType(column.getDataType());
                columnInfo.setComment(column.getComment());
                columnInfos.add(columnInfo);
            }
            previewVO.setColumns(columnInfos);

            return previewVO;
        } catch (Exception e) {
            log.error("Failed to generate preview", e);
            throw new BusinessException("GENERATE_PREVIEW_ERROR", new Object[]{"生成预览失败: " + e.getMessage()});
        }
    }

    @Override
    public Long executeDataGeneration(DataGenerationRequest request) {
        try {
            saveConfigs(request);

            List<ColumnConfigParam> columns = resolveColumns(request);
            if (columns == null) {
                throw new BusinessException("GET_TABLE_COLUMNS_ERROR", new Object[]{"获取表列信息失败"});
            }

            TaskCreateParam taskParam = new TaskCreateParam();
            taskParam.setDataSourceId(request.getDataSourceId());
            taskParam.setDatabaseName(request.getDatabaseName());
            taskParam.setSchemaName(request.getSchemaName());
            taskParam.setTableName(request.getTableName());
            taskParam.setTaskType(TaskTypeEnum.GENERATE_TABLE_DATA.name());
            taskParam.setTaskName("数据生成 - " + request.getTableName());
            taskParam.setTaskProgress("0");

            Long taskId = taskService.create(taskParam);
            if (taskId == null) {
                throw new BusinessException("CREATE_TASK_ERROR", new Object[]{"创建任务失败"});
            }

            LoginUser loginUser = ContextUtils.getLoginUser();
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo().copy();

            CompletableFuture.runAsync(() -> {
                buildContext(loginUser, connectInfo);
                try {
                    executeDataGenerationAsync(taskId, request);
                } finally {
                    removeContext();
                }
            });

            return taskId;
        } catch (Exception e) {
            log.error("Failed to execute data generation", e);
            throw new BusinessException("EXECUTE_GENERATION_ERROR", new Object[]{"执行数据生成失败: " + e.getMessage()});
        }
    }

    @Override
    public List<GeneratorTemplate> getAllGeneratorTemplates() {
        return GeneratorTemplate.getDefaultTemplates();
    }

    private void saveConfigs(DataGenerationRequest request) {
        if (request.getColumnConfigs() == null || request.getColumnConfigs().isEmpty()) {
            return;
        }
        try {
            ruleService.saveColumnConfigs(
                    request.getDataSourceId(), request.getDatabaseName(), request.getSchemaName(),
                    request.getTableName(), 0L, request.getColumnConfigs(), request.getRowCount());
        } catch (Exception e) {
            log.warn("Failed to save generation configs, but continuing operation", e);
        }
    }

    private List<ColumnConfigParam> resolveColumns(DataGenerationRequest request) {
        List<ColumnConfigParam> result = getTableColumns(request);
        if (result == null || result.isEmpty()) {
            return null;
        }
        List<ColumnConfigParam> dbColumns = result;

        if (request.getColumnConfigs() != null && !request.getColumnConfigs().isEmpty()) {
            Map<String, String> expressionMap = request.getColumnConfigs().stream()
                    .filter(c -> c.getExpression() != null)
                    .collect(Collectors.toMap(ColumnConfigParam::getColumnName, ColumnConfigParam::getExpression));

            for (ColumnConfigParam col : dbColumns) {
                String userExpression = expressionMap.get(col.getColumnName());
                if (userExpression != null) {
                    col.setExpression(userExpression);
                }
            }
        }

        return dbColumns;
    }

    private List<Map<String, Object>> generateDataRows(DataGenerationRequest request,
                                                       List<ColumnConfigParam> columns,
                                                       int rowCount) {
        List<Map<String, Object>> dataRows = new ArrayList<>();
        Faker faker = new Faker(LocaleContextHolder.getLocale());

        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (ColumnConfigParam column : columns) {
                if (Boolean.TRUE.equals(column.getAutoIncrement())) {
                    continue;
                }
                String expression = column.getExpression();

                ExpressionDataGenerator.ColumnGenerationConfig config =
                        new ExpressionDataGenerator.ColumnGenerationConfig(
                                column.getColumnName(),
                                column.getDataType(),
                                column.getNullable(),
                                column.getMaxLength(),
                                column.getScale()
                        );

                Object value = expressionDataGenerator.generate(faker, expression, config);
                row.put(column.getColumnName(), value);
            }
            dataRows.add(row);
        }

        return dataRows;
    }

    private void executeDataGenerationAsync(Long taskId, DataGenerationRequest request) {
        Exception error = null;
        try {
            updateTaskProgress(taskId, TaskStatusEnum.PROCESSING, 0);

            List<ColumnConfigParam> columns = resolveColumns(request);
            if (columns == null) {
                throw new RuntimeException("获取表列信息失败");
            }

            int totalRows = request.getRowCount() != null ? request.getRowCount() : 100;
            int batchSize = request.getBatchSize() != null ? request.getBatchSize() : 1000;
            int processedRows = 0;

            while (processedRows < totalRows) {
                int currentBatchSize = Math.min(batchSize, totalRows - processedRows);
                List<Map<String, Object>> batchData = generateDataRows(request, columns, currentBatchSize);
                insertBatchData(request, batchData);
                processedRows += currentBatchSize;

                int progress = (processedRows * 100) / totalRows;
                updateTaskProgress(taskId, TaskStatusEnum.PROCESSING, progress);
            }

            log.info("Data generation completed successfully for table: {}", request.getTableName());
        } catch (Exception e) {
            log.error("Data generation failed for table: " + request.getTableName(), e);
            error = e;
        } finally {
            updateGenerationStatus(taskId, error);
        }
    }

    private void updateGenerationStatus(Long taskId, Exception throwable) {
        try {
            TaskUpdateParam updateParam = new TaskUpdateParam();
            updateParam.setId(taskId);
            if (throwable != null) {
                updateParam.setTaskStatus(TaskStatusEnum.ERROR.name());
                if (throwable instanceof BusinessException businessException) {
                    updateParam.setContent(I18nUtils.getMessage(businessException.getCode(), businessException.getArgs()));
                } else {
                    updateParam.setContent(throwable.getMessage());
                }
            } else {
                updateParam.setTaskStatus(TaskStatusEnum.FINISH.name());
                updateParam.setTaskProgress("100");
            }
            taskService.updateStatus(updateParam);
        } catch (Exception e) {
            log.error("Failed to update generation status", e);
        }
    }

    private void insertBatchData(DataGenerationRequest request, List<Map<String, Object>> batchData) {
        if (batchData == null || batchData.isEmpty()) {
            return;
        }

        List<String> columnNames = new ArrayList<>(batchData.get(0).keySet());
        if (columnNames.isEmpty()) {
            return;
        }

        MetaData metaData = Chat2DBContext.getMetaData();
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(buildTableName(request, metaData)).append(" (");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(metaData.getMetaDataName(columnNames.get(i)));
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");

        Connection connection = Chat2DBContext.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                for (Map<String, Object> row : batchData) {
                    for (int i = 0; i < columnNames.size(); i++) {
                        ps.setObject(i + 1, row.get(columnNames.get(i)));
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                log.error("Batch insert error, SQL: {}", sql, e);
                throw new BusinessException("dataGeneration.batchInsertFailed", new Object[]{e.getMessage()}, e);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            log.error("Batch insert error, SQL: {}", sql, e);
            throw new BusinessException("dataGeneration.batchInsertFailed", new Object[]{e.getMessage()}, e);
        }
    }

    private String buildTableName(DataGenerationRequest request, MetaData metaData) {
        String tableName = metaData.getMetaDataName(request.getTableName());
        if (request.getSchemaName() != null && !request.getSchemaName().isBlank()) {
            tableName = metaData.getMetaDataName(request.getSchemaName()) + "." + tableName;
        }
        if (request.getDatabaseName() != null && !request.getDatabaseName().isBlank()) {
            tableName = metaData.getMetaDataName(request.getDatabaseName()) + "." + tableName;
        }
        return tableName;
    }

    private void updateTaskProgress(Long taskId, TaskStatusEnum status, int progress) {
        try {
            TaskUpdateParam updateParam = new TaskUpdateParam();
            updateParam.setId(taskId);
            updateParam.setTaskStatus(status.name());
            updateParam.setTaskProgress(String.valueOf(progress));
            taskService.updateStatus(updateParam);
        } catch (Exception e) {
            log.error("Failed to update task progress", e);
        }
    }

    private void removeContext() {
        Dbutils.removeSession();
        ContextUtils.removeContext();
        Chat2DBContext.removeContext();
    }

    private void buildContext(LoginUser loginUser, ConnectInfo connectInfo) {
        ContextUtils.setContext(Context.builder()
                .loginUser(loginUser)
                .build());
        Dbutils.setSession();
        Chat2DBContext.putContext(connectInfo);
    }
}

