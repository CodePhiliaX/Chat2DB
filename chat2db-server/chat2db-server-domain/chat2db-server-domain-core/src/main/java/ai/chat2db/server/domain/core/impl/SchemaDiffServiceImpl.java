package ai.chat2db.server.domain.core.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import ai.chat2db.spi.SqlBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.model.schemaDiff.ColumnDiff;
import ai.chat2db.server.domain.api.model.schemaDiff.DiffSummary;
import ai.chat2db.server.domain.api.model.schemaDiff.ForeignKeyDiff;
import ai.chat2db.server.domain.api.model.schemaDiff.IndexDiff;
import ai.chat2db.server.domain.api.model.schemaDiff.SchemaDiffResult;
import ai.chat2db.server.domain.api.model.schemaDiff.TableDiff;
import ai.chat2db.server.domain.api.model.schemaDiff.TableDiffType;
import ai.chat2db.server.domain.api.param.DeprecatedTableParam;
import ai.chat2db.server.domain.api.param.schemaDiff.CompareOption;
import ai.chat2db.server.domain.api.param.schemaDiff.MigrateResult;
import ai.chat2db.server.domain.api.param.schemaDiff.MigrationStatementResult;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaCompareParam;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaMigrateParam;
import ai.chat2db.server.domain.api.service.DataSourceAccessBusinessService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.DeprecatedTableService;
import ai.chat2db.server.domain.api.service.SchemaDiffService;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.ForeignKey;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchemaDiffServiceImpl implements SchemaDiffService {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataSourceAccessBusinessService dataSourceAccessBusinessService;

    @Autowired
    private DeprecatedTableService deprecatedTableService;

    @Override
    public SchemaDiffResult compare(SchemaCompareParam param) {
        Connection sourceConn = null;
        Connection targetConn = null;
        try {
            String sourceDbType = getDbType(param.getSourceDataSourceId());
            String targetDbType = getDbType(param.getTargetDataSourceId());

            Plugin sourcePlugin = Chat2DBContext.PLUGIN_MAP.get(sourceDbType);
            Plugin targetPlugin = Chat2DBContext.PLUGIN_MAP.get(targetDbType);

            if (sourcePlugin == null || targetPlugin == null) {
                throw new RuntimeException("Plugin not found for database type: " +
                        (sourcePlugin == null ? sourceDbType : targetDbType));
            }

            MetaData sourceMeta = sourcePlugin.getMetaData();
            MetaData targetMeta = targetPlugin.getMetaData();

            sourceConn = createConnection(param.getSourceDataSourceId(), param.getSourceDatabaseName(),
                    param.getSourceSchemaName());
            targetConn = createConnection(param.getTargetDataSourceId(), param.getTargetDatabaseName(),
                    param.getTargetSchemaName());

            CompareOption option = param.getCompareOption();
            if (option == null) {
                option = new CompareOption();
            }

            Set<String> sourceDeprecated = Collections.emptySet();
            Set<String> targetDeprecated = Collections.emptySet();
            if (option.isExcludeDeprecated()) {
                sourceDeprecated = queryDeprecatedTableNames(param.getSourceDataSourceId(),
                        param.getSourceDatabaseName(), param.getSourceSchemaName());
                targetDeprecated = queryDeprecatedTableNames(param.getTargetDataSourceId(),
                        param.getTargetDatabaseName(), param.getTargetSchemaName());
            }

            List<String> sourceTableNames = listTableNames(sourceConn, sourceMeta,
                    param.getSourceDatabaseName(), param.getSourceSchemaName(), param.getTableNames(), sourceDeprecated);
            List<String> targetTableNames = listTableNames(targetConn, targetMeta,
                    param.getTargetDatabaseName(), param.getTargetSchemaName(), param.getTableNames(), targetDeprecated);

            Set<String> allTableNames = new HashSet<>();
            allTableNames.addAll(sourceTableNames);
            allTableNames.addAll(targetTableNames);

            List<TableDiff> tableDiffs = new ArrayList<>();
            int onlyInSource = 0, onlyInTarget = 0, modified = 0, unchanged = 0;

            for (String tableName : allTableNames) {
                boolean inSource = sourceTableNames.contains(tableName);
                boolean inTarget = targetTableNames.contains(tableName);

                if (inSource && !inTarget) {
                    Table sourceTable = fetchTableDetails(sourceConn, sourceMeta,
                            param.getSourceDatabaseName(), param.getSourceSchemaName(), tableName);
                    if (sourceTable == null) {
                        log.warn("Source table {} not found, skipping", tableName);
                        onlyInSource++;
                        continue;
                    }
                    String ddl = sourcePlugin.getMetaData().getSqlBuilder().buildCreateTableSql(sourceTable);
                    tableDiffs.add(TableDiff.builder()
                            .tableName(tableName)
                            .diffType(TableDiffType.ADDED)
                            .sourceTable(sourceTable)
                            .ddlStatement(ddl)
                            .ddlStatements(Collections.singletonList(ddl))
                            .build());
                    onlyInSource++;
                } else if (!inSource && inTarget) {
                    Table targetTable = fetchTableDetails(targetConn, targetMeta,
                            param.getTargetDatabaseName(), param.getTargetSchemaName(), tableName);
                    tableDiffs.add(TableDiff.builder()
                            .tableName(tableName)
                            .diffType(TableDiffType.REMOVED)
                            .targetTable(targetTable)
                            .ddlStatements(Collections.emptyList())
                            .build());
                    onlyInTarget++;
                } else {
                    Table sourceTable = fetchTableDetails(sourceConn, sourceMeta,
                            param.getSourceDatabaseName(), param.getSourceSchemaName(), tableName);
                    Table targetTable = fetchTableDetails(targetConn, targetMeta,
                            param.getTargetDatabaseName(), param.getTargetSchemaName(), tableName);

                    List<ColumnDiff> columnDiffs = option.isCompareColumn()
                            ? compareColumns(sourceTable.getColumnList(), targetTable.getColumnList())
                            : Collections.emptyList();
                    List<IndexDiff> indexDiffs = option.isCompareIndex()
                            ? compareIndexes(sourceTable.getIndexList(), targetTable.getIndexList())
                            : Collections.emptyList();
                    List<ForeignKeyDiff> fkDiffs = option.isCompareForeignKey()
                            ? compareForeignKeys(sourceTable.getForeignKeyList(), targetTable.getForeignKeyList())
                            : Collections.emptyList();

                    boolean hasChanges = !columnDiffs.isEmpty() || !indexDiffs.isEmpty() || !fkDiffs.isEmpty();

                    if (option.isCompareTableOption()) {
                        hasChanges = hasChanges || !tableOptionsEqual(sourceTable, targetTable);
                    }

                    if (hasChanges) {
                        Table tableForDDL = buildTableWithEditStatus(sourceTable, targetTable, columnDiffs, indexDiffs, fkDiffs);
                        SqlBuilder sqlBuilder = targetPlugin.getMetaData().getSqlBuilder();
                        String alterDdl = sqlBuilder.buildModifyTaleSql(sourceTable, tableForDDL);

                        tableDiffs.add(TableDiff.builder()
                                .tableName(tableName)
                                .diffType(TableDiffType.MODIFIED)
                                .sourceTable(sourceTable)
                                .targetTable(targetTable)
                                .columnDiffs(columnDiffs)
                                .indexDiffs(indexDiffs)
                                .foreignKeyDiffs(fkDiffs)
                                .ddlStatement(alterDdl)
                                .ddlStatements(StringUtils.isNotBlank(alterDdl)
                                        ? Collections.singletonList(alterDdl)
                                        : Collections.emptyList())
                                .build());
                        modified++;
                    } else {
                        tableDiffs.add(TableDiff.builder()
                                .tableName(tableName)
                                .diffType(TableDiffType.UNCHANGED)
                                .sourceTable(sourceTable)
                                .targetTable(targetTable)
                                .build());
                        unchanged++;
                    }
                }
            }

            int excluded = 0;
            if (option.isExcludeDeprecated()) {
                excluded = (int) (sourceDeprecated.size() + targetDeprecated.size()
                        - new HashSet<>(sourceDeprecated).stream().filter(targetDeprecated::contains).count());
            }

            DiffSummary summary = DiffSummary.builder()
                    .totalTables(allTableNames.size() + excluded)
                    .tablesOnlyInSource(onlyInSource)
                    .tablesOnlyInTarget(onlyInTarget)
                    .modifiedTables(modified)
                    .unchangedTables(unchanged)
                    .excludedDeprecatedTables(excluded)
                    .build();

            String sourceKey = param.getSourceDataSourceId() + "." + param.getSourceDatabaseName()
                    + (param.getSourceSchemaName() != null ? "." + param.getSourceSchemaName() : "");
            String targetKey = param.getTargetDataSourceId() + "." + param.getTargetDatabaseName()
                    + (param.getTargetSchemaName() != null ? "." + param.getTargetSchemaName() : "");

            return SchemaDiffResult.builder()
                    .sourceKey(sourceKey)
                    .targetKey(targetKey)
                    .summary(summary)
                    .tableDiffs(tableDiffs)
                    .build();

        } catch (Exception e) {
            log.error("Schema compare failed", e);
            throw new RuntimeException("Schema compare failed: " + e.getMessage(), e);
        } finally {
            closeQuietly(sourceConn);
            closeQuietly(targetConn);
        }
    }

    @Override
    public MigrateResult migrate(SchemaMigrateParam param) {
        if (CollectionUtils.isEmpty(param.getDdlStatements())) {
            return MigrateResult.builder()
                    .success(true)
                    .totalStatements(0)
                    .successCount(0)
                    .failCount(0)
                    .statementResults(Collections.emptyList())
                    .build();
        }

        Connection conn = null;
        boolean originalAutoCommit = true;
        try {
            conn = createConnection(param.getTargetDataSourceId(), param.getTargetDatabaseName(),
                    param.getTargetSchemaName());

            if (param.isExecuteInTransaction()) {
                originalAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
            }

            Plugin plugin = Chat2DBContext.PLUGIN_MAP.get(
                    getDbType(param.getTargetDataSourceId()));
            ai.chat2db.spi.CommandExecutor executor = plugin.getMetaData().getCommandExecutor();

            List<MigrationStatementResult> results = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < param.getDdlStatements().size(); i++) {
                String sql = param.getDdlStatements().get(i);
                if (StringUtils.isBlank(sql)) {
                    continue;
                }
                long start = System.currentTimeMillis();
                try {
                    ai.chat2db.spi.model.Command command = new ai.chat2db.spi.model.Command();
                    command.setScript(sql);
                    List<ai.chat2db.spi.model.ExecuteResult> executeResults = executor.execute(command);
                    long duration = System.currentTimeMillis() - start;
                    boolean statementSuccess = executeResults != null
                            && executeResults.stream().allMatch(r -> r.getSuccess() != null && r.getSuccess());
                    if (statementSuccess) {
                        successCount++;
                    } else {
                        failCount++;
                        if (!param.isContinueOnError()) {
                            String msg = executeResults != null && !executeResults.isEmpty()
                                    ? executeResults.get(0).getMessage()
                                    : "Unknown error";
                            results.add(MigrationStatementResult.builder()
                                    .sequence(i + 1)
                                    .sql(sql)
                                    .success(false)
                                    .errorMessage(msg)
                                    .duration(duration)
                                    .build());
                            if (param.isExecuteInTransaction()) {
                                conn.rollback();
                            }
                            break;
                        }
                    }
                    results.add(MigrationStatementResult.builder()
                            .sequence(i + 1)
                            .sql(sql)
                            .success(statementSuccess)
                            .errorMessage(statementSuccess ? null : (executeResults != null && !executeResults.isEmpty()
                                    ? executeResults.get(0).getMessage() : "Unknown error"))
                            .duration(duration)
                            .build());
                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - start;
                    failCount++;
                    results.add(MigrationStatementResult.builder()
                            .sequence(i + 1)
                            .sql(sql)
                            .success(false)
                            .errorMessage(e.getMessage())
                            .duration(duration)
                            .build());
                    if (param.isExecuteInTransaction()) {
                        conn.rollback();
                    }
                    if (!param.isContinueOnError()) {
                        break;
                    }
                }
            }

            if (param.isExecuteInTransaction() && failCount == 0) {
                conn.commit();
            }

            return MigrateResult.builder()
                    .success(failCount == 0)
                    .statementResults(results)
                    .totalStatements(results.size())
                    .successCount(successCount)
                    .failCount(failCount)
                    .build();
        } catch (SQLException e) {
            log.error("Migration failed with SQL error", e);
            throw new RuntimeException("Migration failed: " + e.getMessage(), e);
        } finally {
            if (conn != null && param.isExecuteInTransaction()) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    log.warn("Failed to restore auto-commit", e);
                }
            }
            closeQuietly(conn);
        }
    }

    private String getDbType(Long dataSourceId) {
        DataSource ds = dataSourceService.queryById(dataSourceId);
        return ds.getType();
    }

    private Connection createConnection(Long dataSourceId, String databaseName, String schemaName) {
        DataSource dataSource = dataSourceService.queryById(dataSourceId);
        if (dataSource == null) {
            throw new RuntimeException("DataSource not found: " + dataSourceId);
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
        return plugin.getDBManage().getConnection(connectInfo);
    }

    private Set<String> queryDeprecatedTableNames(Long dataSourceId, String databaseName, String schemaName) {
        DeprecatedTableParam param = new DeprecatedTableParam();
        param.setUserId(ContextUtils.getUserId());
        param.setDataSourceId(dataSourceId);
        param.setDatabaseName(databaseName);
        param.setSchemaName(schemaName);
        List<String> list = deprecatedTableService.queryDeprecatedTables(param);
        return new HashSet<>(list);
    }

    private List<String> listTableNames(Connection conn, MetaData meta, String databaseName, String schemaName,
                                         List<String> specificTables, Set<String> deprecatedSet) {
        List<Table> tables = meta.tables(conn, databaseName, schemaName, null);
        if (CollectionUtils.isEmpty(tables)) {
            return Collections.emptyList();
        }
        return tables.stream()
                .map(Table::getName)
                .filter(name -> CollectionUtils.isEmpty(specificTables) || specificTables.contains(name))
                .filter(name -> !deprecatedSet.contains(name))
                .collect(Collectors.toList());
    }

    private Table fetchTableDetails(Connection conn, MetaData meta, String databaseName, String schemaName,
                                     String tableName) {
        List<Table> tables = meta.tables(conn, databaseName, schemaName, tableName);
        if (CollectionUtils.isEmpty(tables)) {
            return null;
        }
        Table table = tables.get(0);
        try {
            table.setColumnList(meta.columns(conn, databaseName, schemaName, tableName));
        } catch (Exception e) {
            log.warn("Failed to fetch columns for table {}: {}", tableName, e.getMessage());
            table.setColumnList(Collections.emptyList());
        }
        try {
            table.setIndexList(meta.indexes(conn, databaseName, schemaName, tableName));
        } catch (Exception e) {
            log.warn("Failed to fetch indexes for table {}: {}", tableName, e.getMessage());
            table.setIndexList(Collections.emptyList());
        }
        try {
            table.setForeignKeyList(meta.foreignKeys(conn, databaseName, schemaName, tableName));
        } catch (Exception e) {
            log.warn("Failed to fetch foreign keys for table {}: {}", tableName, e.getMessage());
            table.setForeignKeyList(Collections.emptyList());
        }
        return table;
    }

    private List<ColumnDiff> compareColumns(List<TableColumn> sourceCols, List<TableColumn> targetCols) {
        List<ColumnDiff> diffs = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceCols) && CollectionUtils.isEmpty(targetCols)) {
            return diffs;
        }
        Map<String, TableColumn> sourceMap = CollectionUtils.isEmpty(sourceCols)
                ? Collections.emptyMap()
                : sourceCols.stream().filter(c -> c.getName() != null)
                        .collect(Collectors.toMap(TableColumn::getName, c -> c, (a, b) -> a));
        Map<String, TableColumn> targetMap = CollectionUtils.isEmpty(targetCols)
                ? Collections.emptyMap()
                : targetCols.stream().filter(c -> c.getName() != null)
                        .collect(Collectors.toMap(TableColumn::getName, c -> c, (a, b) -> a));

        for (Map.Entry<String, TableColumn> entry : targetMap.entrySet()) {
            String colName = entry.getKey();
            TableColumn targetCol = entry.getValue();
            TableColumn sourceCol = sourceMap.get(colName);
            if (sourceCol == null) {
                diffs.add(ColumnDiff.builder()
                        .changeType(EditStatus.ADD)
                        .targetColumn(targetCol)
                        .build());
            } else if (!columnEquals(sourceCol, targetCol)) {
                diffs.add(ColumnDiff.builder()
                        .changeType(EditStatus.MODIFY)
                        .sourceColumn(sourceCol)
                        .targetColumn(targetCol)
                        .build());
            }
        }
        for (Map.Entry<String, TableColumn> entry : sourceMap.entrySet()) {
            if (!targetMap.containsKey(entry.getKey())) {
                diffs.add(ColumnDiff.builder()
                        .changeType(EditStatus.DELETE)
                        .sourceColumn(entry.getValue())
                        .build());
            }
        }
        return diffs;
    }

    private boolean columnEquals(TableColumn a, TableColumn b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.getDataType(), b.getDataType())
                && Objects.equals(a.getColumnSize(), b.getColumnSize())
                && Objects.equals(a.getDecimalDigits(), b.getDecimalDigits())
                && Objects.equals(a.getNullable(), b.getNullable())
                && Objects.equals(a.getDefaultValue(), b.getDefaultValue())
                && Objects.equals(a.getComment(), b.getComment())
                && Objects.equals(a.getAutoIncrement(), b.getAutoIncrement())
                && Objects.equals(a.getCharSetName(), b.getCharSetName())
                && Objects.equals(a.getCollationName(), b.getCollationName())
                && Objects.equals(a.getColumnType(), b.getColumnType());
    }

    private List<IndexDiff> compareIndexes(List<TableIndex> sourceIdxs, List<TableIndex> targetIdxs) {
        List<IndexDiff> diffs = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceIdxs) && CollectionUtils.isEmpty(targetIdxs)) {
            return diffs;
        }
        Map<String, TableIndex> sourceMap = CollectionUtils.isEmpty(sourceIdxs)
                ? Collections.emptyMap()
                : sourceIdxs.stream().filter(i -> i.getName() != null)
                        .collect(Collectors.toMap(TableIndex::getName, i -> i, (a, b) -> a));
        Map<String, TableIndex> targetMap = CollectionUtils.isEmpty(targetIdxs)
                ? Collections.emptyMap()
                : targetIdxs.stream().filter(i -> i.getName() != null)
                        .collect(Collectors.toMap(TableIndex::getName, i -> i, (a, b) -> a));

        for (Map.Entry<String, TableIndex> entry : targetMap.entrySet()) {
            String idxName = entry.getKey();
            TableIndex targetIdx = entry.getValue();
            TableIndex sourceIdx = sourceMap.get(idxName);
            if (sourceIdx == null) {
                diffs.add(IndexDiff.builder()
                        .changeType(EditStatus.ADD)
                        .targetIndex(targetIdx)
                        .build());
            } else if (!indexEquals(sourceIdx, targetIdx)) {
                diffs.add(IndexDiff.builder()
                        .changeType(EditStatus.MODIFY)
                        .sourceIndex(sourceIdx)
                        .targetIndex(targetIdx)
                        .build());
            }
        }
        for (Map.Entry<String, TableIndex> entry : sourceMap.entrySet()) {
            if (!targetMap.containsKey(entry.getKey())) {
                diffs.add(IndexDiff.builder()
                        .changeType(EditStatus.DELETE)
                        .sourceIndex(entry.getValue())
                        .build());
            }
        }
        return diffs;
    }

    private boolean indexEquals(TableIndex a, TableIndex b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.getType(), b.getType())
                && Objects.equals(a.getUnique(), b.getUnique())
                && Objects.equals(a.getMethod(), b.getMethod())
                && Objects.equals(a.getComment(), b.getComment());
    }

    private List<ForeignKeyDiff> compareForeignKeys(List<ForeignKey> sourceFKs, List<ForeignKey> targetFKs) {
        List<ForeignKeyDiff> diffs = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceFKs) && CollectionUtils.isEmpty(targetFKs)) {
            return diffs;
        }
        Map<String, ForeignKey> sourceMap = CollectionUtils.isEmpty(sourceFKs)
                ? Collections.emptyMap()
                : sourceFKs.stream().filter(fk -> fk.getName() != null)
                        .collect(Collectors.toMap(ForeignKey::getName, fk -> fk, (a, b) -> a));
        Map<String, ForeignKey> targetMap = CollectionUtils.isEmpty(targetFKs)
                ? Collections.emptyMap()
                : targetFKs.stream().filter(fk -> fk.getName() != null)
                        .collect(Collectors.toMap(ForeignKey::getName, fk -> fk, (a, b) -> a));

        for (Map.Entry<String, ForeignKey> entry : targetMap.entrySet()) {
            String fkName = entry.getKey();
            ForeignKey targetFK = entry.getValue();
            ForeignKey sourceFK = sourceMap.get(fkName);
            if (sourceFK == null) {
                diffs.add(ForeignKeyDiff.builder()
                        .changeType(EditStatus.ADD)
                        .targetForeignKey(targetFK)
                        .build());
            } else if (!foreignKeyEquals(sourceFK, targetFK)) {
                diffs.add(ForeignKeyDiff.builder()
                        .changeType(EditStatus.MODIFY)
                        .sourceForeignKey(sourceFK)
                        .targetForeignKey(targetFK)
                        .build());
            }
        }
        for (Map.Entry<String, ForeignKey> entry : sourceMap.entrySet()) {
            if (!targetMap.containsKey(entry.getKey())) {
                diffs.add(ForeignKeyDiff.builder()
                        .changeType(EditStatus.DELETE)
                        .sourceForeignKey(entry.getValue())
                        .build());
            }
        }
        return diffs;
    }

    private boolean foreignKeyEquals(ForeignKey a, ForeignKey b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.getColumn(), b.getColumn())
                && Objects.equals(a.getReferencedTable(), b.getReferencedTable())
                && Objects.equals(a.getReferencedColumn(), b.getReferencedColumn())
                && Objects.equals(a.getUpdateRule(), b.getUpdateRule())
                && Objects.equals(a.getDeleteRule(), b.getDeleteRule());
    }

    private boolean tableOptionsEqual(Table a, Table b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.getComment(), b.getComment())
                && Objects.equals(a.getEngine(), b.getEngine())
                && Objects.equals(a.getCharset(), b.getCharset())
                && Objects.equals(a.getCollate(), b.getCollate())
                && Objects.equals(a.getIncrementValue(), b.getIncrementValue());
    }

    private Table buildTableWithEditStatus(Table sourceTable, Table targetTable,
                                            List<ColumnDiff> columnDiffs, List<IndexDiff> indexDiffs,
                                            List<ForeignKeyDiff> fkDiffs) {
        Table result = new Table();
        result.setName(targetTable.getName());
        result.setComment(targetTable.getComment());
        result.setDatabaseName(targetTable.getDatabaseName());
        result.setSchemaName(targetTable.getSchemaName());
        result.setEngine(targetTable.getEngine());
        result.setCharset(targetTable.getCharset());
        result.setCollate(targetTable.getCollate());
        result.setIncrementValue(targetTable.getIncrementValue());
        result.setPartition(targetTable.getPartition());

        if (CollectionUtils.isEmpty(columnDiffs)) {
            result.setColumnList(targetTable.getColumnList() != null
                    ? new ArrayList<>(targetTable.getColumnList()) : new ArrayList<>());
        } else {
            List<TableColumn> columns = new ArrayList<>();
            if (targetTable.getColumnList() != null) {
                Map<String, TableColumn> targetColMap = targetTable.getColumnList().stream()
                        .filter(c -> c.getName() != null)
                        .collect(Collectors.toMap(TableColumn::getName, c -> c, (a, b) -> a));
                Map<String, ColumnDiff> addModifyMap = columnDiffs.stream()
                        .filter(d -> d.getChangeType() == EditStatus.ADD || d.getChangeType() == EditStatus.MODIFY)
                        .filter(d -> d.getTargetColumn() != null && d.getTargetColumn().getName() != null)
                        .collect(Collectors.toMap(d -> d.getTargetColumn().getName(), d -> d, (a, b) -> a));

                for (TableColumn col : targetTable.getColumnList()) {
                    ColumnDiff diff = addModifyMap.get(col.getName());
                    if (diff != null) {
                        col.setEditStatus(diff.getChangeType().name());
                        if (diff.getChangeType() == EditStatus.MODIFY && diff.getSourceColumn() != null
                                && !Objects.equals(diff.getSourceColumn().getName(), col.getName())) {
                            col.setOldName(diff.getSourceColumn().getName());
                        }
                    }
                    columns.add(col);
                }
            }
            for (ColumnDiff diff : columnDiffs) {
                if (diff.getChangeType() == EditStatus.DELETE && diff.getSourceColumn() != null) {
                    TableColumn deletedCol = new TableColumn();
                    deletedCol.setName(diff.getSourceColumn().getName());
                    deletedCol.setOldName(diff.getSourceColumn().getName());
                    deletedCol.setEditStatus(EditStatus.DELETE.name());
                    columns.add(deletedCol);
                }
            }
            result.setColumnList(columns);
        }

        if (CollectionUtils.isEmpty(indexDiffs)) {
            result.setIndexList(targetTable.getIndexList() != null
                    ? new ArrayList<>(targetTable.getIndexList()) : new ArrayList<>());
        } else {
            List<TableIndex> indexes = new ArrayList<>();
            if (targetTable.getIndexList() != null) {
                Map<String, TableIndex> targetIdxMap = targetTable.getIndexList().stream()
                        .filter(i -> i.getName() != null)
                        .collect(Collectors.toMap(TableIndex::getName, i -> i, (a, b) -> a));
                Map<String, IndexDiff> addModifyMap = indexDiffs.stream()
                        .filter(d -> d.getChangeType() == EditStatus.ADD || d.getChangeType() == EditStatus.MODIFY)
                        .filter(d -> d.getTargetIndex() != null && d.getTargetIndex().getName() != null)
                        .collect(Collectors.toMap(d -> d.getTargetIndex().getName(), d -> d, (a, b) -> a));
                for (TableIndex idx : targetTable.getIndexList()) {
                    IndexDiff diff = addModifyMap.get(idx.getName());
                    if (diff != null) {
                        idx.setEditStatus(diff.getChangeType().name());
                        if (diff.getChangeType() == EditStatus.MODIFY && diff.getSourceIndex() != null) {
                            idx.setOldName(diff.getSourceIndex().getName());
                        }
                    }
                    indexes.add(idx);
                }
            }
            for (IndexDiff diff : indexDiffs) {
                if (diff.getChangeType() == EditStatus.DELETE && diff.getSourceIndex() != null) {
                    TableIndex deletedIdx = new TableIndex();
                    deletedIdx.setName(diff.getSourceIndex().getName());
                    deletedIdx.setOldName(diff.getSourceIndex().getName());
                    deletedIdx.setEditStatus(EditStatus.DELETE.name());
                    indexes.add(deletedIdx);
                }
            }
            result.setIndexList(indexes);
        }

        if (CollectionUtils.isEmpty(fkDiffs)) {
            result.setForeignKeyList(targetTable.getForeignKeyList() != null
                    ? new ArrayList<>(targetTable.getForeignKeyList()) : new ArrayList<>());
        } else {
            List<ForeignKey> fks = new ArrayList<>();
            if (targetTable.getForeignKeyList() != null) {
                Map<String, ForeignKey> targetFKMap = targetTable.getForeignKeyList().stream()
                        .filter(fk -> fk.getName() != null)
                        .collect(Collectors.toMap(ForeignKey::getName, fk -> fk, (a, b) -> a));
                Map<String, ForeignKeyDiff> addModifyMap = fkDiffs.stream()
                        .filter(d -> d.getChangeType() == EditStatus.ADD || d.getChangeType() == EditStatus.MODIFY)
                        .filter(d -> d.getTargetForeignKey() != null && d.getTargetForeignKey().getName() != null)
                        .collect(Collectors.toMap(d -> d.getTargetForeignKey().getName(), d -> d, (a, b) -> a));
                for (ForeignKey fk : targetTable.getForeignKeyList()) {
                    ForeignKeyDiff diff = addModifyMap.get(fk.getName());
                    if (diff != null) {
                        fk.setEditStatus(diff.getChangeType().name());
                    }
                    fks.add(fk);
                }
            }
            for (ForeignKeyDiff diff : fkDiffs) {
                if (diff.getChangeType() == EditStatus.DELETE && diff.getSourceForeignKey() != null) {
                    ForeignKey deletedFK = new ForeignKey();
                    deletedFK.setName(diff.getSourceForeignKey().getName());
                    deletedFK.setEditStatus(EditStatus.DELETE.name());
                    fks.add(deletedFK);
                }
            }
            result.setForeignKeyList(fks);
        }

        return result;
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.warn("Error closing connection", e);
            }
        }
    }
}
