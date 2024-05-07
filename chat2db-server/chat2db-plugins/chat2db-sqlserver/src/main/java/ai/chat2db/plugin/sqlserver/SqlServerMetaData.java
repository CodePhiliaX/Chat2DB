package ai.chat2db.plugin.sqlserver;

import ai.chat2db.plugin.sqlserver.builder.SqlServerSqlBuilder;
import ai.chat2db.plugin.sqlserver.type.SqlServerColumnTypeEnum;
import ai.chat2db.plugin.sqlserver.type.SqlServerDefaultValueEnum;
import ai.chat2db.plugin.sqlserver.type.SqlServerIndexTypeEnum;
import ai.chat2db.spi.CommandExecutor;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SortUtils;
import cn.hutool.core.util.ObjectUtil;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static ai.chat2db.spi.util.SortUtils.sortDatabase;

public class SqlServerMetaData extends DefaultMetaService implements MetaData {


    private List<String> systemDatabases = Arrays.asList("master", "model", "msdb", "tempdb");

    @Override
    public List<Database> databases(Connection connection) {
        List<Database> databases = SQLExecutor.getInstance().databases(connection);
        return sortDatabase(databases, systemDatabases, connection);
    }

    private List<String> systemSchemas = Arrays.asList("guest", "INFORMATION_SCHEMA", "sys", "db_owner",
                                                       "db_accessadmin", "db_securityadmin", "db_ddladmin", "db_backupoperator", "db_datareader", "db_datawriter",
                                                       "db_denydatareader", "db_denydatawriter");

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = SQLExecutor.getInstance().schemas(connection, databaseName, null);
        return SortUtils.sortSchema(schemas, systemSchemas);
    }

    private static final String SELECT_TABLE_COMMENT_SQL = """
                                                           SELECT
                                                               t.name AS TableName,
                                                               p.value AS TABLE_COMMENT
                                                           FROM
                                                               sys.tables t
                                                           JOIN
                                                               sys.extended_properties p ON t.object_id = p.major_id
                                                           WHERE
                                                               p.minor_id = 0 AND p.class = 1 AND p.name = 'MS_Description'
                                                               AND t.name = '%s'
                                                               AND SCHEMA_NAME(t.schema_id) = '%s';""";

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append("[").append(schemaName).append("].[").append(tableName).append("] (").append("\n");
        try (ResultSet columns = connection.createStatement().executeQuery(String.format(SELECT_TABLE_COLUMNS, tableName, schemaName));
             ResultSet indexInfo = connection.createStatement().executeQuery(String.format(INDEX_SQL, tableName, schemaName));
             ResultSet tableComment = connection.createStatement().executeQuery(String.format(SELECT_TABLE_COMMENT_SQL, tableName, schemaName));
        ) {
            List<TableColumn> tableColumns = new ArrayList<>();
            while (columns.next()) {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setSchemaName(schemaName);
                tableColumn.setTableName(tableName);
                tableColumn.setName(columns.getString("COLUMN_NAME"));
                tableColumn.setColumnType(columns.getString("DATA_TYPE").toUpperCase());
                if (ObjectUtil.notEqual(SqlServerColumnTypeEnum.FLOAT.name(), tableColumn.getColumnType())
                        && ObjectUtil.notEqual(SqlServerColumnTypeEnum.REAL.name(), tableColumn.getColumnType())) {
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    int numericScale = columns.getInt("NUMERIC_SCALE");
                    if ((Objects.equals(SqlServerColumnTypeEnum.NCHAR.name(), tableColumn.getColumnType())
                            || Objects.equals(SqlServerColumnTypeEnum.NVARCHAR.name(), tableColumn.getColumnType()))) {
                        columnSize = columnSize / 2;
                    }
                    if (Objects.equals(SqlServerColumnTypeEnum.DATETIMEOFFSET.name(), tableColumn.getColumnType())
                            || Objects.equals(SqlServerColumnTypeEnum.TIME.name(), tableColumn.getColumnType())
                            || Objects.equals(SqlServerColumnTypeEnum.DATETIME2.name(), tableColumn.getColumnType())) {
                        tableColumn.setColumnSize(numericScale);
                    } else {
                        tableColumn.setColumnSize(columnSize);
                    }
                    if (ObjectUtil.notEqual(SqlServerColumnTypeEnum.DATETIME2.name(), tableColumn.getColumnType())
                            && ObjectUtil.notEqual(SqlServerColumnTypeEnum.DATETIMEOFFSET.name(), tableColumn.getColumnType())
                            && ObjectUtil.notEqual(SqlServerColumnTypeEnum.TIME.name(), tableColumn.getColumnType())) {
                        tableColumn.setDecimalDigits(numericScale);
                    }
                }
                tableColumn.setSparse(columns.getBoolean("IS_SPARSE"));
                tableColumn.setDefaultValue(columns.getString("COLUMN_DEFAULT"));
                tableColumn.setNullable(columns.getInt("IS_NULLABLE"));
                tableColumn.setCollationName(columns.getString("COLLATION_NAME"));
                tableColumn.setComment(columns.getString("COLUMN_COMMENT"));
                tableColumns.add(tableColumn);
                SqlServerColumnTypeEnum typeEnum = SqlServerColumnTypeEnum.getByType(tableColumn.getColumnType());
                sqlBuilder.append("\t").append(typeEnum.buildCreateColumnSql(tableColumn)).append(",\n");
            }
            sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.length() - 2));
            sqlBuilder.append("\n)\ngo\n");
            if (CollectionUtils.isEmpty(tableColumns)) {
                return sqlBuilder.toString();
            }
            HashMap<String, TableIndex> indexHashMap = new HashMap<>();
            while (indexInfo.next()) {
                String indexName = indexInfo.getString("INDEX_NAME");
                TableIndex index = indexHashMap.get(indexName);
                if (Objects.isNull(index)) {
                    index = new TableIndex();
                    index.setSchemaName(schemaName);
                    index.setTableName(tableName);
                    index.setName(indexName);
                    boolean isPrimaryKey = indexInfo.getBoolean("IS_PRIMARY");
                    if (isPrimaryKey) {
                        index.setType(SqlServerIndexTypeEnum.PRIMARY_KEY.getName());
                    } else {
                        String indexType = indexInfo.getString("INDEX_TYPE");
                        boolean isUnique = indexInfo.getBoolean("IS_UNIQUE");
                        if (isUnique) {
                            if (Objects.equals(SqlServerIndexTypeEnum.NONCLUSTERED.name(), indexType)) {
                                index.setType(SqlServerIndexTypeEnum.UNIQUE_NONCLUSTERED.getName());
                            } else {
                                index.setType(SqlServerIndexTypeEnum.UNIQUE_CLUSTERED.getName());
                            }
                        } else {
                            index.setType(indexType);
                        }
                    }
                    index.setColumnList(new ArrayList<>());
                    indexHashMap.put(indexName, index);
                }
                index.setComment(indexInfo.getString("INDEX_COMMENT"));
                List<TableIndexColumn> columnList = index.getColumnList();
                TableIndexColumn tableIndexColumn = new TableIndexColumn();
                tableIndexColumn.setTableName(tableName);
                tableIndexColumn.setSchemaName(schemaName);
                tableIndexColumn.setColumnName(indexInfo.getString("COLUMN_NAME"));
                boolean descend = indexInfo.getBoolean("DESCEND");
                if (descend) {
                    tableIndexColumn.setAscOrDesc("DESC");
                } else {
                    tableIndexColumn.setAscOrDesc("ASC");
                }
                columnList.add(tableIndexColumn);
            }
            for (TableIndex index : indexHashMap.values()) {
                SqlServerIndexTypeEnum sqlServerIndexTypeEnum = SqlServerIndexTypeEnum.getByType(index.getType());
                sqlBuilder.append("\n").append(sqlServerIndexTypeEnum.buildIndexScript(index));
                if (StringUtils.isNotBlank(index.getComment())) {
                    sqlBuilder.append("\n").append(buildIndexComment(index));
                }
            }
            for (TableColumn column : tableColumns) {
                if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(column.getColumnType())
                        || StringUtils.isBlank(column.getComment())) {
                    continue;
                }
                sqlBuilder.append("\n").append(buildColumnComment(column));
            }

            if (tableComment.next()) {
                String comment = tableComment.getString("TABLE_COMMENT");
                if (StringUtils.isNotBlank(comment)) {
                    Table table = new Table();
                    table.setComment(comment);
                    table.setName(tableName);
                    table.setSchemaName(schemaName);
                    sqlBuilder.append("\n").append(buildTableComment(table));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sqlBuilder.toString();
    }

    private static String INDEX_COMMENT_SCRIPT = "exec sp_addextendedproperty 'MS_Description','%s','SCHEMA','%s','TABLE','%s','INDEX','%s' \ngo";


    private String buildIndexComment(TableIndex tableIndex) {
        return String.format(INDEX_COMMENT_SCRIPT, tableIndex.getComment(), tableIndex.getSchemaName(), tableIndex.getTableName(), tableIndex.getName());
    }

    private static String COLUMN_COMMENT_SCRIPT = "exec sp_addextendedproperty 'MS_Description','%s','SCHEMA','%s','TABLE','%s','COLUMN','%s' \ngo";

    private String buildColumnComment(TableColumn column) {
        return String.format(COLUMN_COMMENT_SCRIPT, column.getComment(), column.getSchemaName(), column.getTableName(), column.getName());
    }

    private static String TABLE_COMMENT_SCRIPT = "exec sp_addextendedproperty 'MS_Description','%s','SCHEMA','%s','TABLE','%s' \ngo";


    private String buildTableComment(Table table) {
        return String.format(TABLE_COMMENT_SCRIPT, table.getComment(), table.getSchemaName(), table.getName());
    }

    private static String SELECT_TABLES_SQL = "SELECT t.name AS TableName, mm.value as comment FROM sys.tables t LEFT JOIN(SELECT * from sys.extended_properties ep where ep.minor_id = 0 AND ep.name = 'MS_Description') mm ON t.object_id = mm.major_id WHERE t.schema_id= SCHEMA_ID('%S')";

    @Override
    public List<Table> tables(Connection connection, String databaseName, String schemaName, String tableName) {
        List<Table> tables = new ArrayList<>();
        String sql = String.format(SELECT_TABLES_SQL, schemaName);
        if (StringUtils.isNotBlank(tableName)) {
            sql += " AND t.name = '" + tableName + "'";
        } else {
            sql += " ORDER BY t.name";
        }

        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Table table = new Table();
                table.setDatabaseName(databaseName);
                table.setSchemaName(schemaName);
                table.setName(resultSet.getString("TableName"));
                table.setComment(resultSet.getString("comment"));
                tables.add(table);
            }
            return tables;
        });
    }

    private static final String SELECT_TABLE_COLUMNS = """
                                                       SELECT c.name           as COLUMN_NAME,
                                                              c.is_sparse      as IS_SPARSE,
                                                              c.is_nullable    as IS_NULLABLE,
                                                              c.column_id      as ORDINAL_POSITION,
                                                              c.max_length     as COLUMN_SIZE,
                                                              c.scale          as NUMERIC_SCALE,
                                                              c.collation_name as COLLATION_NAME,
                                                              ty.name          as DATA_TYPE,
                                                              t.name,
                                                              def.definition   as COLUMN_DEFAULT,
                                                              ep.value         as COLUMN_COMMENT
                                                       from sys.columns c
                                                                LEFT JOIN sys.tables t on c.object_id = t.object_id
                                                                LEFT JOIN sys.types ty ON c.user_type_id = ty.user_type_id
                                                                LEFT JOIN sys.default_constraints def ON c.default_object_id = def.object_id
                                                                LEFT JOIN sys.extended_properties ep ON t.object_id = ep.major_id AND c.column_id = ep.minor_id and class_desc!='INDEX'
                                                       WHERE t.name = '%s'
                                                         and t.schema_id = SCHEMA_ID('%s');""";

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(SELECT_TABLE_COLUMNS, tableName, schemaName);
        List<TableColumn> tableColumns = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                TableColumn column = new TableColumn();
                column.setDatabaseName(databaseName);
                column.setTableName(tableName);
                column.setSchemaName(schemaName);
                column.setOldName(resultSet.getString("COLUMN_NAME"));
                column.setName(resultSet.getString("COLUMN_NAME"));
                //column.setColumnType(resultSet.getString("COLUMN_TYPE"));
                column.setColumnType(resultSet.getString("DATA_TYPE").toUpperCase());
                //column.setDataType(resultSet.getInt("DATA_TYPE"));
                column.setDefaultValue(resultSet.getString("COLUMN_DEFAULT"));
                //column.setAutoIncrement(resultSet.getString("EXTRA").contains("auto_increment"));
                column.setComment(resultSet.getString("COLUMN_COMMENT"));
                // column.setPrimaryKey("PRI".equalsIgnoreCase(resultSet.getString("COLUMN_KEY")));
                column.setNullable(resultSet.getInt("IS_NULLABLE"));
                column.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
                column.setDecimalDigits(resultSet.getInt("NUMERIC_SCALE"));
                // column.setCharSetName(resultSet.getString("CHARACTER_SET_NAME"));
                column.setCollationName(resultSet.getString("COLLATION_NAME"));
                column.setColumnSize(resultSet.getInt("COLUMN_SIZE"));
                //setColumnSize(column, resultSet.getString("COLUMN_TYPE"));
                tableColumns.add(column);
            }
            return tableColumns;
        });
    }

    private static String ROUTINES_SQL
            = "SELECT type_desc, OBJECT_NAME(object_id) AS FunctionName, OBJECT_DEFINITION(object_id) AS "
            + "definition FROM sys.objects WHERE type_desc IN(%s) and name = '%s' ;";


    private static String OBJECT_SQL
            = "SELECT name FROM sys.objects WHERE type = '%s' and SCHEMA_ID = SCHEMA_ID('%s') order by name;";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
                             String functionName) {

        String sql = String.format(ROUTINES_SQL, "'SQL_SCALAR_FUNCTION', 'SQL_TABLE_VALUED_FUNCTION'", functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setFunctionBody(resultSet.getString("definition"));
            }
            return function;
        });
    }

    @Override
    public List<Function> functions(Connection connection, String databaseName, String schemaName) {
        List<Function> functions = new ArrayList<>();
        String sql = String.format(OBJECT_SQL, "FN", schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Function function = new Function();
                function.setDatabaseName(databaseName);
                function.setSchemaName(schemaName);
                function.setFunctionName(resultSet.getString("name"));
                functions.add(function);
            }
            return functions;
        });
    }

    private Function removeVersion(Function function) {
        String fullFunctionName = function.getFunctionName();
        if (!StringUtils.isEmpty(fullFunctionName)) {
            String[] parts = fullFunctionName.split(";");
            String functionName = parts[0];
            function.setFunctionName(functionName);
        }
        return function;
    }

    @Override
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        List<Procedure> procedures = new ArrayList<>();
        String sql = String.format(OBJECT_SQL, "P", schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Procedure procedure = new Procedure();
                procedure.setDatabaseName(databaseName);
                procedure.setSchemaName(schemaName);
                procedure.setProcedureName(resultSet.getString("name"));
                procedures.add(procedure);
            }
            return procedures;
        });
    }

    private Procedure removeVersion(Procedure procedure) {
        String fullProcedureName = procedure.getProcedureName();
        if (!StringUtils.isEmpty(fullProcedureName)) {
            String[] parts = fullProcedureName.split(";");
            String procedureName = parts[0];
            procedure.setProcedureName(procedureName);
        }
        return procedure;
    }

    private static String TRIGGER_SQL
            = "SELECT OBJECT_NAME(parent_obj) AS TableName, name AS triggerName, OBJECT_DEFINITION(id) AS "
            + "triggerDefinition, CASE WHEN status & 1 = 1 THEN 'Enabled' ELSE 'Disabled' END AS Status FROM sysobjects "
            + "WHERE xtype = 'TR' and name = '%s';";

    private static String TRIGGER_SQL_LIST
            = "SELECT OBJECT_NAME(parent_obj) AS TableName, name AS triggerName, OBJECT_DEFINITION(id) AS "
            + "triggerDefinition, CASE WHEN status & 1 = 1 THEN 'Enabled' ELSE 'Disabled' END AS Status FROM sysobjects "
            + "WHERE xtype = 'TR' order by name";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, TRIGGER_SQL_LIST, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setTriggerName(resultSet.getString("triggerName"));
                trigger.setSchemaName(schemaName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });
    }

    @Override
    public Trigger trigger(Connection connection, @NotEmpty String databaseName, String schemaName,
                           String triggerName) {

        String sql = String.format(TRIGGER_SQL, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("triggerDefinition"));
            }
            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
                               String procedureName) {
        String sql = String.format(ROUTINES_SQL, "'SQL_STORED_PROCEDURE'", procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                                                     Procedure procedure = new Procedure();
                                                     procedure.setDatabaseName(databaseName);
                                                     procedure.setSchemaName(schemaName);
                                                     procedure.setProcedureName(procedureName);
                                                     if (resultSet.next()) {
                                                         procedure.setProcedureBody(resultSet.getString("definition"));
                                                     }
                                                     return procedure;
                                                 }
        );
    }

    private static String VIEW_SQL
            = "SELECT TABLE_SCHEMA, TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = '%s' "
            + "AND TABLE_NAME = '%s';";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("VIEW_DEFINITION"));
            }
            return table;
        });
    }

    private static final String INDEX_SQL = """
                                            SELECT ic.key_ordinal       AS COLUMN_POSITION,
                                                   ic.is_descending_key as DESCEND,
                                                   ind.name             AS INDEX_NAME,
                                                   ind.is_unique        AS IS_UNIQUE,
                                                   col.name             AS COLUMN_NAME,
                                                   ind.type_desc        AS INDEX_TYPE,
                                                   ind.is_primary_key   AS IS_PRIMARY,
                                                   ep.value             as INDEX_COMMENT
                                            FROM sys.indexes ind
                                                     INNER JOIN sys.index_columns ic
                                                                ON ind.object_id = ic.object_id and ind.index_id = ic.index_id and ic.key_ordinal > 0
                                                     INNER JOIN sys.columns col ON ic.object_id = col.object_id and ic.column_id = col.column_id
                                                     INNER JOIN sys.tables t ON ind.object_id = t.object_id
                                                     LEFT JOIN sys.key_constraints kc ON ind.object_id = kc.parent_object_id AND ind.index_id = kc.unique_index_id
                                                     LEFT JOIN sys.extended_properties ep ON ind.object_id = ep.major_id AND ind.index_id = ep.minor_id
                                            WHERE t.name = '%s'
                                              and t.schema_id = SCHEMA_ID('%s')
                                            ORDER BY t.name, ind.name, ind.index_id, ic.index_column_id""";

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(INDEX_SQL, tableName, schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            LinkedHashMap<String, TableIndex> map = new LinkedHashMap();
            while (resultSet.next()) {
                String keyName = resultSet.getString("INDEX_NAME");
                TableIndex tableIndex = map.get(keyName);
                if (tableIndex != null) {
                    List<TableIndexColumn> columnList = tableIndex.getColumnList();
                    columnList.add(getTableIndexColumn(resultSet));
                    columnList = columnList.stream().sorted(Comparator.comparing(TableIndexColumn::getOrdinalPosition))
                            .collect(Collectors.toList());
                    tableIndex.setColumnList(columnList);
                } else {
                    TableIndex index = new TableIndex();
                    index.setDatabaseName(databaseName);
                    index.setSchemaName(schemaName);
                    index.setTableName(tableName);
                    index.setName(keyName);
                    int isunique = resultSet.getInt("IS_UNIQUE");
                    if (isunique == 1) {
                        index.setUnique(true);
                    } else {
                        index.setUnique(false);
                    }
                    List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
                    tableIndexColumns.add(getTableIndexColumn(resultSet));
                    index.setColumnList(tableIndexColumns);
                    String indexType = resultSet.getString("INDEX_TYPE");
                    if (resultSet.getBoolean("IS_PRIMARY")) {
                        index.setType(SqlServerIndexTypeEnum.PRIMARY_KEY.getName());
                    } else if ("CLUSTERED".equalsIgnoreCase(indexType)) {
                        if (index.getUnique()) {
                            index.setType(SqlServerIndexTypeEnum.UNIQUE_CLUSTERED.getName());
                        } else {
                            index.setType(SqlServerIndexTypeEnum.CLUSTERED.getName());
                        }
                    } else if ("NONCLUSTERED".equalsIgnoreCase(indexType)) {
                        if (index.getUnique()) {
                            index.setType(SqlServerIndexTypeEnum.UNIQUE_NONCLUSTERED.getName());
                        } else {
                            index.setType(SqlServerIndexTypeEnum.NONCLUSTERED.getName());
                        }
                    } else {
                        index.setType(indexType);
                    }
                    map.put(keyName, index);
                }
            }
            return map.values().stream().collect(Collectors.toList());
        });

    }

    private TableIndexColumn getTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("COLUMN_NAME"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("COLUMN_POSITION"));
        int collation = resultSet.getInt("DESCEND");
        if (collation == 1) {
            tableIndexColumn.setAscOrDesc("ASC");
        } else {
            tableIndexColumn.setAscOrDesc("DESC");
        }
        return tableIndexColumn;
    }


    @Override
    public SqlBuilder getSqlBuilder() {
        return new SqlServerSqlBuilder();
    }

    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(SqlServerColumnTypeEnum.getTypes())
                .charsets(null)
                .collations(null)
                .indexTypes(SqlServerIndexTypeEnum.getIndexTypes())
                .defaultValues(SqlServerDefaultValueEnum.getDefaultValues())
                .build();
    }


    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "[" + name + "]").collect(Collectors.joining("."));
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new SqlServerCommandExecutor();
    }

    @Override
    public List<String> getSystemDatabases() {
        return systemDatabases;
    }

    @Override
    public List<String> getSystemSchemas() {
        return systemSchemas;
    }
}
