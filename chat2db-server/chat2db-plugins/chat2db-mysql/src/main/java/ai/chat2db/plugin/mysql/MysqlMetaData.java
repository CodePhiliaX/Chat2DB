package ai.chat2db.plugin.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import ai.chat2db.plugin.mysql.builder.MysqlSqlBuilder;
import ai.chat2db.plugin.mysql.type.*;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.ValueHandler;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static ai.chat2db.spi.util.SortUtils.sortDatabase;

@Slf4j
public class MysqlMetaData extends DefaultMetaService implements MetaData {

    private List<String> systemDatabases = Arrays.asList("information_schema", "performance_schema", "mysql", "sys");

    private static final String SELECT_TABLES_SQL = "SELECT TABLE_NAME, TABLE_COMMENT, TABLE_ROWS, ENGINE, CREATE_TIME, UPDATE_TIME, AUTO_INCREMENT " +
            "FROM information_schema.tables WHERE TABLE_SCHEMA = '%s' AND TABLE_TYPE IN ('BASE TABLE', 'SYSTEM TABLE')";

    @Override
    public List<Table> tables(Connection connection, @NotEmpty String databaseName, String schemaName, String tableName) {
        List<Table> tables = new ArrayList<>();

        String sql = String.format(SELECT_TABLES_SQL, databaseName);
        if (StringUtils.isNotBlank(tableName)) {
            sql += String.format(" AND TABLE_NAME = '%s'", tableName);
        }
        sql += " ORDER BY TABLE_NAME";

        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                while (resultSet.next()) {
                    Table table = Table.builder()
                            .name(resultSet.getString("TABLE_NAME"))
                            .comment(resultSet.getString("TABLE_COMMENT"))
                            .databaseName(databaseName)
                            .schemaName(schemaName)
                            .type("BASE TABLE")
                            .engine(resultSet.getString("ENGINE"))
                            .build();

                    // 设置预估行数（InnoDB 等引擎可能返回 NULL）
                    long rowCount = resultSet.getLong("TABLE_ROWS");
                    if (!resultSet.wasNull()) {
                        table.setRowCount(rowCount);
                    }

                    // 设置自增列的下一个自增值（可能为 NULL）
                    long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                    if (!resultSet.wasNull()) {
                        table.setIncrementValue(autoIncrement);
                    }

                    tables.add(table);
                }
                return null;
            });
        } catch (Exception e) {
            // 如果查询失败，回退到 JDBC 元数据方式
            return SQLExecutor.getInstance().tables(connection, databaseName, schemaName, tableName,
                    new String[]{"TABLE", "SYSTEM TABLE"});
        }

        return tables;
    }

    @Override
    public List<Database> databases(Connection connection) {
        List<Database> databases = SQLExecutor.getInstance().databases(connection);
        return sortDatabase(databases, systemDatabases, connection);
    }


    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
                           @NotEmpty String tableName) {
        String sql;
        if (StringUtils.isEmpty(databaseName)) {
            sql = "SHOW CREATE TABLE " + format(tableName);
        } else {
            sql = "SHOW CREATE TABLE " + format(databaseName) + "."
                    + format(tableName);
        }
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("Create Table");
            }
            return null;
        });
    }

    public static String format(String tableName) {
        return "`" + tableName + "`";
    }

    private static String ROUTINES_SQL
            =
            "SELECT SPECIFIC_NAME, ROUTINE_COMMENT, ROUTINE_DEFINITION FROM information_schema.routines WHERE "
                    + "routine_type = '%s' AND ROUTINE_SCHEMA ='%s'  AND "
                    + "routine_name = '%s';";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
                             String functionName) {

        Function function = new Function();
        function.setDatabaseName(databaseName);
        function.setSchemaName(schemaName);
        function.setName(functionName);

        // 首先尝试使用 information_schema.routines 获取信息
        String sql = String.format(ROUTINES_SQL, "FUNCTION", databaseName, functionName);
        log.info("[MySQL] Querying function detail: {}", sql);

        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                if (resultSet.next()) {
                    function.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                    function.setComment(resultSet.getString("ROUTINE_COMMENT"));
                    function.setFunctionBody(resultSet.getString("ROUTINE_DEFINITION"));
                    log.info("[MySQL] Function {} found, body length: {}", functionName,
                            function.getFunctionBody() != null ? function.getFunctionBody().length() : 0);
                } else {
                    log.warn("[MySQL] Function {} not found in information_schema.routines", functionName);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("[MySQL] Failed to query function from information_schema: {}", e.getMessage());
        }

        // 如果 ROUTINE_DEFINITION 为空，尝试使用 SHOW CREATE FUNCTION
        if (StringUtils.isBlank(function.getFunctionBody())) {
            String showCreateSql = "SHOW CREATE FUNCTION `" + databaseName + "`.`" + functionName + "`";
            log.info("[MySQL] Trying SHOW CREATE FUNCTION: {}", showCreateSql);

            try {
                SQLExecutor.getInstance().execute(connection, showCreateSql, resultSet -> {
                    if (resultSet.next()) {
                        String createFunc = resultSet.getString("Create Function");
                        if (StringUtils.isNotBlank(createFunc)) {
                            function.setFunctionBody(createFunc);
                            log.info("[MySQL] Got function body from SHOW CREATE FUNCTION, length: {}",
                                    createFunc.length());
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                log.error("[MySQL] SHOW CREATE FUNCTION failed: {}", e.getMessage());
            }
        }

        return function;
    }

    private static String TRIGGER_SQL
            = "SELECT TRIGGER_NAME,EVENT_MANIPULATION, ACTION_STATEMENT  FROM INFORMATION_SCHEMA.TRIGGERS where "
            + "TRIGGER_SCHEMA = '%s' AND TRIGGER_NAME = '%s';";

    private static String TRIGGER_SQL_LIST
            = "SELECT TRIGGER_NAME FROM INFORMATION_SCHEMA.TRIGGERS where TRIGGER_SCHEMA = '%s';";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        String sql = String.format(TRIGGER_SQL_LIST, databaseName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setName(resultSet.getString("TRIGGER_NAME"));
                trigger.setSchemaName(schemaName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });
    }

    /**
     * MySQL 的 JDBC 驱动 getProcedures() 会同时返回 FUNCTION 和 PROCEDURE
     * 这里使用自定义 SQL 只返回 PROCEDURE 类型
     */
    @Override
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        String sql = "SELECT SPECIFIC_NAME, ROUTINE_COMMENT, ROUTINE_SCHEMA " +
                "FROM information_schema.routines " +
                "WHERE ROUTINE_TYPE = 'PROCEDURE' AND ROUTINE_SCHEMA = '" + databaseName + "' " +
                "ORDER BY ROUTINE_NAME";

        log.info("[MySQL] Querying procedures: {}", sql);

        final List<Procedure> resultHolder = new ArrayList<>();
        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                while (resultSet.next()) {
                    Procedure procedure = new Procedure();
                    procedure.setName(resultSet.getString("SPECIFIC_NAME"));
                    procedure.setComment(resultSet.getString("ROUTINE_COMMENT"));
                    procedure.setDatabaseName(databaseName);
                    procedure.setSchemaName(schemaName);
                    resultHolder.add(procedure);
                }
                log.info("[MySQL] Found {} procedures", resultHolder.size());
                return null;
            });
        } catch (Exception e) {
            log.error("[MySQL] Failed to query procedures: {}", e.getMessage());
            // 如果查询失败，回退到 JDBC 元数据方式
            List<Procedure> allProcedures = super.procedures(connection, databaseName, schemaName);
            if (allProcedures != null) {
                return allProcedures;
            }
        }

        return resultHolder;
    }

    /**
     * MySQL 的 JDBC 驱动 getFunctions() 可能返回不准确
     * 这里使用自定义 SQL 只返回 FUNCTION 类型
     */
    @Override
    public List<ai.chat2db.spi.model.Function> functions(Connection connection, String databaseName, String schemaName) {
        String sql = "SELECT SPECIFIC_NAME, ROUTINE_COMMENT, ROUTINE_SCHEMA " +
                "FROM information_schema.routines " +
                "WHERE ROUTINE_TYPE = 'FUNCTION' AND ROUTINE_SCHEMA = '" + databaseName + "' " +
                "ORDER BY ROUTINE_NAME";

        log.info("[MySQL] Querying functions: {}", sql);

        final List<ai.chat2db.spi.model.Function> resultHolder = new ArrayList<>();
        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                while (resultSet.next()) {
                    ai.chat2db.spi.model.Function function = new ai.chat2db.spi.model.Function();
                    function.setName(resultSet.getString("SPECIFIC_NAME"));
                    function.setComment(resultSet.getString("ROUTINE_COMMENT"));
                    function.setDatabaseName(databaseName);
                    function.setSchemaName(schemaName);
                    resultHolder.add(function);
                }
                log.info("[MySQL] Found {} functions", resultHolder.size());
                return null;
            });
        } catch (Exception e) {
            log.error("[MySQL] Failed to query functions: {}", e.getMessage());
            // 如果查询失败，回退到 JDBC 元数据方式
            List<ai.chat2db.spi.model.Function> allFunctions = super.functions(connection, databaseName, schemaName);
            if (allFunctions != null) {
                return allFunctions;
            }
        }

        return resultHolder;
    }

    @Override
    public Trigger trigger(Connection connection, @NotEmpty String databaseName, String schemaName,
                           String triggerName) {

        Trigger trigger = new Trigger();
        trigger.setDatabaseName(databaseName);
        trigger.setSchemaName(schemaName);
        trigger.setName(triggerName);

        String sql = String.format(TRIGGER_SQL, databaseName, triggerName);
        log.info("[MySQL] Querying trigger detail: {}", sql);

        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                if (resultSet.next()) {
                    trigger.setEventManipulation(resultSet.getString("EVENT_MANIPULATION"));
                    trigger.setTriggerBody(resultSet.getString("ACTION_STATEMENT"));
                    log.info("[MySQL] Trigger {} found, body length: {}", triggerName,
                            trigger.getTriggerBody() != null ? trigger.getTriggerBody().length() : 0);
                } else {
                    log.warn("[MySQL] Trigger {} not found in information_schema.triggers", triggerName);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("[MySQL] Failed to query trigger from information_schema: {}", e.getMessage());
        }

        return trigger;
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
                               String procedureName) {
        Procedure procedure = new Procedure();
        procedure.setDatabaseName(databaseName);
        procedure.setSchemaName(schemaName);
        procedure.setName(procedureName);

        // 首先尝试使用 information_schema.routines 获取信息
        String sql = String.format(ROUTINES_SQL, "PROCEDURE", databaseName, procedureName);
        log.info("[MySQL] Querying procedure detail: {}", sql);

        try {
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                if (resultSet.next()) {
                    procedure.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                    procedure.setComment(resultSet.getString("ROUTINE_COMMENT"));
                    procedure.setProcedureBody(resultSet.getString("ROUTINE_DEFINITION"));
                    log.info("[MySQL] Procedure {} found, body length: {}", procedureName,
                            procedure.getProcedureBody() != null ? procedure.getProcedureBody().length() : 0);
                } else {
                    log.warn("[MySQL] Procedure {} not found in information_schema.routines", procedureName);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("[MySQL] Failed to query procedure from information_schema: {}", e.getMessage());
        }

        // 如果 ROUTINE_DEFINITION 为空，尝试使用 SHOW CREATE PROCEDURE
        if (StringUtils.isBlank(procedure.getProcedureBody())) {
            String showCreateSql = "SHOW CREATE PROCEDURE `" + databaseName + "`.`" + procedureName + "`";
            log.info("[MySQL] Trying SHOW CREATE PROCEDURE: {}", showCreateSql);

            try {
                SQLExecutor.getInstance().execute(connection, showCreateSql, resultSet -> {
                    if (resultSet.next()) {
                        String createProc = resultSet.getString("Create Procedure");
                        if (StringUtils.isNotBlank(createProc)) {
                            procedure.setProcedureBody(createProc);
                            log.info("[MySQL] Got procedure body from SHOW CREATE PROCEDURE, length: {}",
                                    createProc.length());
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                log.error("[MySQL] SHOW CREATE PROCEDURE failed: {}", e.getMessage());
            }
        }

        return procedure;
    }

    private static final String SELECT_TABLE_COLUMNS_TEMPLATE = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '%s' %s ORDER BY ORDINAL_POSITION";

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName) {
        // 构建 SQL 查询语句
        String tableCondition = (tableName != null) ? String.format("AND TABLE_NAME = '%s'", tableName) : "";
        String sql = String.format(SELECT_TABLE_COLUMNS_TEMPLATE, databaseName, tableCondition);

        List<TableColumn> tableColumns = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                TableColumn column = new TableColumn();
                column.setDatabaseName(databaseName);
                column.setTableName(resultSet.getString("TABLE_NAME"));
                column.setOldName(resultSet.getString("COLUMN_NAME"));
                column.setName(resultSet.getString("COLUMN_NAME"));
                column.setColumnType(resultSet.getString("COLUMN_TYPE"));
                column.setDataType(resultSet.getString("DATA_TYPE"));
                column.setDefaultValue(resultSet.getString("COLUMN_DEFAULT"));
                String extra = resultSet.getString("EXTRA");
                column.setAutoIncrement(extra != null && extra.contains("auto_increment"));
                column.setComment(resultSet.getString("COLUMN_COMMENT"));
                column.setPrimaryKey("PRI".equalsIgnoreCase(resultSet.getString("COLUMN_KEY")));
                column.setNullable("YES".equalsIgnoreCase(resultSet.getString("IS_NULLABLE")) ? 1 : 0);
                column.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
                column.setDecimalDigits(resultSet.getInt("NUMERIC_SCALE"));
                column.setCharSetName(resultSet.getString("CHARACTER_SET_NAME"));
                column.setCollationName(resultSet.getString("COLLATION_NAME"));
                setColumnSize(column, resultSet.getString("COLUMN_TYPE"));
                tableColumns.add(column);
            }
            return tableColumns;
        });
    }


    private void setColumnSize(TableColumn column, String columnType) {
        try {
            if (columnType.contains("(")) {
                String size = columnType.substring(columnType.indexOf("(") + 1, columnType.indexOf(")"));
                if ("SET".equalsIgnoreCase(column.getColumnType()) || "ENUM".equalsIgnoreCase(column.getColumnType())) {
                    column.setValue(size);
                } else {
                    if (size.contains(",")) {
                        String[] sizes = size.split(",");
                        if (StringUtils.isNotBlank(sizes[0])) {
                            column.setColumnSize(Integer.parseInt(sizes[0]));
                        }
                        if (StringUtils.isNotBlank(sizes[1])) {
                            column.setDecimalDigits(Integer.parseInt(sizes[1]));
                        }
                    } else {
                        column.setColumnSize(Integer.parseInt(size));
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    private static String VIEW_SQL
            = "SELECT TABLE_SCHEMA AS DatabaseName, TABLE_NAME AS ViewName, VIEW_DEFINITION AS definition, CHECK_OPTION, "
            + "IS_UPDATABLE FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, databaseName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("definition"));
            }
            return table;
        });
    }


    /**
     * 批量查询索引的 SQL 模板
     * 使用 information_schema.STATISTICS 可以一次性获取所有表的索引信息
     */
    private static final String SELECT_INDEXES_SQL = 
            "SELECT TABLE_NAME, INDEX_NAME, NON_UNIQUE, INDEX_TYPE, COLUMN_NAME, SEQ_IN_INDEX, " +
            "COLLATION, CARDINALITY, SUB_PART, INDEX_COMMENT " +
            "FROM information_schema.STATISTICS " +
            "WHERE TABLE_SCHEMA = '%s' %s " +
            "ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        // 构建 SQL 查询：支持单表查询和批量查询
        String tableCondition = (tableName != null) 
                ? String.format("AND TABLE_NAME = '%s'", tableName) 
                : "";
        String sql = String.format(SELECT_INDEXES_SQL, databaseName, tableCondition);
        
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            // 使用嵌套 Map：外层 key 为 tableName，内层 key 为 indexName
            Map<String, LinkedHashMap<String, TableIndex>> tableIndexMap = new HashMap<>();
            
            while (resultSet.next()) {
                String currentTableName = resultSet.getString("TABLE_NAME");
                String keyName = resultSet.getString("INDEX_NAME");
                
                // 获取或创建当前表的索引映射
                LinkedHashMap<String, TableIndex> indexMap = tableIndexMap.computeIfAbsent(
                        currentTableName, k -> new LinkedHashMap<>());
                
                TableIndex tableIndex = indexMap.get(keyName);
                if (tableIndex != null) {
                    // 索引已存在，添加列信息
                    List<TableIndexColumn> columnList = tableIndex.getColumnList();
                    columnList.add(getTableIndexColumn(resultSet));
                    // 保持按 Seq_in_index 排序
                    columnList.sort(Comparator.comparing(TableIndexColumn::getOrdinalPosition));
                } else {
                    // 新索引，创建并初始化
                    TableIndex index = createTableIndex(resultSet, databaseName, schemaName, currentTableName, keyName);
                    indexMap.put(keyName, index);
                }
            }
            
            // 如果指定了 tableName，只返回该表的索引
            if (tableName != null) {
                LinkedHashMap<String, TableIndex> indexMap = tableIndexMap.get(tableName);
                return indexMap != null 
                        ? new ArrayList<>(indexMap.values()) 
                        : Collections.emptyList();
            }
            
            // 否则返回所有表的索引
            List<TableIndex> allIndexes = new ArrayList<>();
            tableIndexMap.values().forEach(indexMap -> allIndexes.addAll(indexMap.values()));
            return allIndexes;
        });
    }

    /**
     * 从 ResultSet 创建 TableIndex 对象
     */
    private TableIndex createTableIndex(ResultSet resultSet, String databaseName, String schemaName, 
                                         String tableName, String keyName) throws SQLException {
        TableIndex index = new TableIndex();
        index.setDatabaseName(databaseName);
        index.setSchemaName(schemaName);
        index.setTableName(tableName);
        index.setName(keyName);
        index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
        
        String indexType = resultSet.getString("INDEX_TYPE");
        index.setComment(resultSet.getString("INDEX_COMMENT"));
        
        List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
        tableIndexColumns.add(getTableIndexColumn(resultSet));
        index.setColumnList(tableIndexColumns);
        
        // 根据索引名称和属性判断索引类型
        if ("PRIMARY".equalsIgnoreCase(keyName)) {
            index.setType(MysqlIndexTypeEnum.PRIMARY_KEY.getName());
        } else if (index.getUnique()) {
            index.setType(MysqlIndexTypeEnum.UNIQUE.getName());
        } else if ("SPATIAL".equalsIgnoreCase(indexType)) {
            index.setType(MysqlIndexTypeEnum.SPATIAL.getName());
        } else if ("FULLTEXT".equalsIgnoreCase(indexType)) {
            index.setType(MysqlIndexTypeEnum.FULLTEXT.getName());
        } else {
            index.setType(MysqlIndexTypeEnum.NORMAL.getName());
        }
        
        return index;
    }

    private TableIndexColumn getTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("Column_name"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("Seq_in_index"));
        tableIndexColumn.setCollation(resultSet.getString("Collation"));
        tableIndexColumn.setCardinality(resultSet.getLong("Cardinality"));
        tableIndexColumn.setSubPart(resultSet.getLong("Sub_part"));
        String collation = resultSet.getString("Collation");
        if ("a".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("ASC");
        } else if ("d".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("DESC");
        }
        return tableIndexColumn;
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new MysqlSqlBuilder();
    }

    @Override
    public TableMeta getTableMeta(Connection connection, String databaseName, String schemaName) {
        return TableMeta.builder()
                .columnTypes(MysqlColumnTypeEnum.getTypes())
                .charsets(MysqlCharsetEnum.getCharsets())
                .collations(MysqlCollationEnum.getCollations())
                .indexTypes(MysqlIndexTypeEnum.getIndexTypes())
                .defaultValues(MysqlDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "`" + name + "`").collect(Collectors.joining("."));
    }

    @Override
    public ValueHandler getValueHandler() {
        return new MysqlValueHandler();
    }
}
