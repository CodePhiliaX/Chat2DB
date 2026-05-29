package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.domain.api.enums.TaskStatusEnum;
import ai.chat2db.server.domain.api.enums.TaskTypeEnum;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.DataSourceAccessBusinessService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.task.request.DataTransferRequest;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransferBizService {

    private static final int BATCH_SIZE = 200;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataSourceAccessBusinessService dataSourceAccessBusinessService;

    public DataResult<Long> transferData(DataTransferRequest request) {
        Long taskId = createTransferTask(request);
        LoginUser loginUser = ContextUtils.getLoginUser();

        CompletableFuture.runAsync(() -> {
            ContextUtils.setContext(Context.builder().loginUser(loginUser).build());
            Dbutils.setSession();
            doTransferData(request, taskId);
        }).whenComplete((aVoid, throwable) -> {
            updateTransferStatus(taskId, throwable);
            Dbutils.removeSession();
            ContextUtils.removeContext();
            Chat2DBContext.removeContext();
        });

        return DataResult.of(taskId);
    }

    private Long createTransferTask(DataTransferRequest request) {
        TaskCreateParam param = new TaskCreateParam();
        param.setTaskName("transfer_" + buildTaskPart(request.getSourceDatabaseName(), request.getSourceSchemaName())
                + "_to_" + buildTaskPart(request.getTargetDatabaseName(), request.getTargetSchemaName()));
        param.setTaskType(TaskTypeEnum.TRANSFER_TABLE_DATA.name());
        param.setDatabaseName(request.getTargetDatabaseName());
        param.setSchemaName(request.getTargetSchemaName());
        param.setTableName(StringUtils.join(request.getTableNames(), ","));
        param.setDataSourceId(request.getTargetDataSourceId());
        param.setUserId(ContextUtils.getUserId());
        param.setTaskProgress("0");
        return taskService.create(param);
    }

    private String buildTaskPart(String databaseName, String schemaName) {
        List<String> names = new ArrayList<>();
        names.add(databaseName);
        names.add(schemaName);
        String taskPart = names.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("_"));
        return StringUtils.isBlank(taskPart) ? "default" : taskPart;
    }

    private void doTransferData(DataTransferRequest request, Long taskId) {
        int totalCount = 0;
        try (Connection sourceConnection = createConnection(request.getSourceDataSourceId(),
                request.getSourceDatabaseName(), request.getSourceSchemaName());
             Connection targetConnection = createConnection(request.getTargetDataSourceId(),
                     request.getTargetDatabaseName(), request.getTargetSchemaName())) {

            targetConnection.setAutoCommit(true);

            for (String tableName : request.getTableNames()) {
                updateContent(taskId, "Transferring table: " + tableName);
                totalCount = transferTable(sourceConnection, targetConnection, request, tableName, taskId, totalCount);
            }
        } catch (Exception e) {
            log.error("transfer data error", e);
            throw e instanceof BusinessException ? (BusinessException)e
                    : new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }
    }

    private int transferTable(Connection sourceConnection, Connection targetConnection, DataTransferRequest request,
                              String tableName, Long taskId, int totalCount) throws SQLException {
        List<String> sourceColumns = queryColumns(sourceConnection, request.getSourceDatabaseName(),
                request.getSourceSchemaName(), tableName);
        List<String> targetColumns = queryColumns(targetConnection, request.getTargetDatabaseName(),
                request.getTargetSchemaName(), tableName);

        if (targetColumns.isEmpty()) {
            throw new BusinessException("Target table not found: " + tableName);
        }

        List<ColumnMapping> columnMappings = intersectColumns(sourceColumns, targetColumns);
        if (columnMappings.isEmpty()) {
            throw new BusinessException("No common columns for table: " + tableName);
        }

        List<String> sourceTransferColumns = columnMappings.stream().map(ColumnMapping::sourceColumn).toList();
        List<String> targetTransferColumns = columnMappings.stream().map(ColumnMapping::targetColumn).toList();
        String selectSql = buildSelectSql(sourceConnection, request.getSourceSchemaName(), tableName,
                sourceTransferColumns);
        String insertSql = buildInsertSql(targetConnection, request.getTargetSchemaName(), tableName,
                targetTransferColumns);

        try (PreparedStatement selectStatement = sourceConnection.prepareStatement(selectSql,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             PreparedStatement insertStatement = targetConnection.prepareStatement(insertSql)) {
            selectStatement.setFetchSize(BATCH_SIZE);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int tableCount = 0;

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    insertStatement.setObject(i, resultSet.getObject(i));
                }
                insertStatement.addBatch();
                tableCount++;
                totalCount++;
                if (tableCount % BATCH_SIZE == 0) {
                    insertStatement.executeBatch();
                    insertStatement.clearBatch();
                    updateProgressCount(taskId, totalCount);
                }
            }

            insertStatement.executeBatch();
            insertStatement.clearBatch();
            updateProgressCount(taskId, totalCount);
            updateContent(taskId, "Finished table: " + tableName + ", rows: " + tableCount);
            return totalCount;
            }
        } catch (SQLException e) {
            throw new BusinessException("Transfer table " + tableName + " failed: " + e.getMessage(),
                    new Object[]{e.getMessage()}, e);
        }
    }

    private List<String> queryColumns(Connection connection, String databaseName, String schemaName, String tableName)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> columns = readColumns(metaData, databaseName, schemaName, tableName);
        if (!columns.isEmpty()) {
            return columns;
        }
        columns = readColumns(metaData, null, schemaName, tableName);
        if (!columns.isEmpty()) {
            return columns;
        }
        return readColumns(metaData, null, null, tableName);
    }

    private List<String> readColumns(DatabaseMetaData metaData, String databaseName, String schemaName, String tableName)
            throws SQLException {
        List<String> columns = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(databaseName, schemaName, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }

    private List<ColumnMapping> intersectColumns(List<String> sourceColumns, List<String> targetColumns) {
        Map<String, String> targetColumnMap = targetColumns.stream()
                .collect(Collectors.toMap(name -> name.toLowerCase(Locale.ROOT), name -> name, (left, right) -> left));
        return sourceColumns.stream()
                .filter(name -> targetColumnMap.containsKey(name.toLowerCase(Locale.ROOT)))
                .map(name -> new ColumnMapping(name, targetColumnMap.get(name.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
    }

    private String buildSelectSql(Connection connection, String schemaName, String tableName, List<String> columns)
            throws SQLException {
        String columnSql = buildColumnSql(connection, columns);
        return "SELECT " + columnSql + " FROM " + qualifiedTableName(connection, schemaName, tableName);
    }

    private String buildInsertSql(Connection connection, String schemaName, String tableName, List<String> columns)
            throws SQLException {
        String columnSql = buildColumnSql(connection, columns);
        String placeholders = columns.stream().map(column -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + qualifiedTableName(connection, schemaName, tableName)
                + " (" + columnSql + ") VALUES (" + placeholders + ")";
    }

    private String buildColumnSql(Connection connection, List<String> columns) throws SQLException {
        List<String> quotedColumns = new ArrayList<>();
        for (String column : columns) {
            quotedColumns.add(quoteIdentifier(connection, column));
        }
        return StringUtils.join(quotedColumns, ", ");
    }

    private String qualifiedTableName(Connection connection, String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return quoteIdentifier(connection, tableName);
        }
        return quoteIdentifier(connection, schemaName) + "." + quoteIdentifier(connection, tableName);
    }

    private String quoteIdentifier(Connection connection, String identifier) throws SQLException {
        String quote = connection.getMetaData().getIdentifierQuoteString();
        if (StringUtils.isBlank(quote)) {
            return identifier;
        }
        String trimmedQuote = quote.trim();
        if (StringUtils.isBlank(trimmedQuote)) {
            return identifier;
        }
        return trimmedQuote + identifier.replace(trimmedQuote, trimmedQuote + trimmedQuote) + trimmedQuote;
    }

    private Connection createConnection(Long dataSourceId, String databaseName, String schemaName) {
        DataSource dataSource = dataSourceService.queryById(dataSourceId);
        if (dataSource == null) {
            throw new BusinessException("DataSource not found: " + dataSourceId);
        }
        dataSourceAccessBusinessService.checkPermission(dataSource);

        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setUser(dataSource.getUserName());
        connectInfo.setPassword(dataSource.getPassword());
        connectInfo.setDbType(dataSource.getType());
        connectInfo.setUrl(dataSource.getUrl());
        connectInfo.setDatabase(databaseName);
        connectInfo.setSchemaName(schemaName);
        connectInfo.setDriver(dataSource.getDriver());
        connectInfo.setSsh(dataSource.getSsh());
        connectInfo.setSsl(dataSource.getSsl());
        connectInfo.setJdbc(dataSource.getJdbc());
        connectInfo.setExtendInfo(dataSource.getExtendInfo());
        connectInfo.setHost(dataSource.getHost());
        if (StringUtils.isNotBlank(dataSource.getPort())) {
            connectInfo.setPort(Integer.parseInt(dataSource.getPort()));
        }
        ai.chat2db.spi.config.DriverConfig driverConfig = dataSource.getDriverConfig();
        if (driverConfig == null) {
            driverConfig = Chat2DBContext.getDefaultDriverConfig(dataSource.getType());
        }
        connectInfo.setDriverConfig(driverConfig);
        connectInfo.setConsoleOwn(false);

        Plugin plugin = Chat2DBContext.PLUGIN_MAP.get(dataSource.getType());
        if (plugin == null) {
            throw new BusinessException("Unsupported data source type: " + dataSource.getType());
        }

        ConnectInfo previousInfo = Chat2DBContext.getConnectInfo();
        try {
            Chat2DBContext.putContext(connectInfo);
            return plugin.getDBManage().getConnection(connectInfo);
        } finally {
            if (previousInfo != null) {
                Chat2DBContext.putContext(previousInfo);
            } else {
                Chat2DBContext.remove();
            }
        }
    }

    private void updateProgressCount(Long taskId, int processedCount) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(taskId);
        updateParam.setTaskProgress(String.valueOf(processedCount));
        taskService.updateStatus(updateParam);
    }

    private void updateContent(Long taskId, String content) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(taskId);
        updateParam.setContent(content);
        taskService.updateStatus(updateParam);
    }

    private void updateTransferStatus(Long id, Throwable throwable) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(id);
        if (throwable != null) {
            log.error("transfer error", throwable);
            updateParam.setTaskStatus(TaskStatusEnum.ERROR.name());
            Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
            updateParam.setContent(cause.getMessage());
        } else {
            updateParam.setTaskStatus(TaskStatusEnum.FINISH.name());
        }
        taskService.updateStatus(updateParam);
    }

    private record ColumnMapping(String sourceColumn, String targetColumn) {
    }
}
