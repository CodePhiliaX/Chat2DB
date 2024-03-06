package ai.chat2db.plugin.snowflake;

import ai.chat2db.plugin.snowflake.builder.SnowflakeSqlBuilder;
import ai.chat2db.plugin.snowflake.type.*;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SortUtils;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SnowflakeMetaData extends DefaultMetaService implements MetaData {


    private List<String> systemSchemas = Arrays.asList("INFORMATION_SCHEMA", "PUBLIC", "SCHEMA");

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = SQLExecutor.getInstance().schemas(connection, databaseName, null);
        return SortUtils.sortSchema(schemas, systemSchemas);
    }

    private static String VIEW_SQL
            = "SELECT TABLE_SCHEMA AS DatabaseName, TABLE_NAME AS ViewName, VIEW_DEFINITION AS DEFINITION, CHECK_OPTION, "
            + "IS_UPDATABLE FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_CATALOG = '%s' AND TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";


    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, databaseName, schemaName , viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("DEFINITION").substring(resultSet.getString("DEFINITION").indexOf("as")+3));
            }
            return table;
        });
    }


    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(SnowflakeColumnTypeEnum.getTypes())
                .charsets(SnowflakeCharsetEnum.getCharsets())
                .collations(SnowflakeCollationEnum.getCollations())
                .indexTypes(SnowflakeIndexTypeEnum.getIndexTypes())
                .defaultValues(SnowflakeDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new SnowflakeSqlBuilder();
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "\"" + name + "\"").collect(Collectors.joining("."));
    }

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        // 目前仅能查看主键
        StringBuilder queryBuf = new StringBuilder("SHOW PRIMARY KEYS in ");
        queryBuf.append("\"").append(tableName).append("\"");
        return SQLExecutor.getInstance().execute(connection, queryBuf.toString(), resultSet -> {
            LinkedHashMap<String, TableIndex> map = new LinkedHashMap();
            while (resultSet.next()) {
                String keyName = resultSet.getString("constraint_name");
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
                    //index.setUnique(!resultSet.getBoolean("Non_unique"));
                    index.setType(SnowflakeIndexTypeEnum.PRIMARY_KEY.getName());
                    index.setComment(resultSet.getString("comment"));
                    List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
                    tableIndexColumns.add(getTableIndexColumn(resultSet));
                    index.setColumnList(tableIndexColumns);
                    if ("PRIMARY".equalsIgnoreCase(keyName)) {
                        index.setType(SnowflakeIndexTypeEnum.PRIMARY_KEY.getName());
                    }
                    map.put(keyName, index);
                }
            }
            return map.values().stream().collect(Collectors.toList());
        });
    }

    private TableIndexColumn getTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("column_name"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("key_sequence"));
        //tableIndexColumn.setCollation(resultSet.getString("Collation"));
        //tableIndexColumn.setCardinality(resultSet.getLong("Cardinality"));
        //tableIndexColumn.setSubPart(resultSet.getLong("Sub_part"));
        /*String collation = resultSet.getString("Collation");
        if ("a".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("ASC");
        } else if ("d".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("DESC");
        }*/
        return tableIndexColumn;
    }

    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
                           @NotEmpty String tableName) {
        // 需要后续自己实现。目前没有办法直接获取建表语句。
        return "";
        /*String sql = "SHOW CREATE TABLE " + format(schemaName) + "."
                + format(tableName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("Create Table");
            }
            return null;
        });*/
    }

    private static String OBJECT_SQL
            = "SHOW USER FUNCTIONS IN SCHEMA \"%s\"";

    @Override
    public List<Function> functions(Connection connection, String databaseName, String schemaName) {
        List<Function> functions = new ArrayList<>();
        String sql = String.format(OBJECT_SQL, schemaName);
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

    private static String ROUTINES_SQL
            =
            "SELECT FUNCTION_NAME, FUNCTION_DEFINITION, COMMENT " +
                    "FROM INFORMATION_SCHEMA.FUNCTIONS " +
                    "WHERE FUNCTION_SCHEMA = '%s'  AND FUNCTION_NAME = '%s';";
    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
                             String functionName) {

        String sql = String.format(ROUTINES_SQL, schemaName, functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setSpecificName(resultSet.getString("FUNCTION_NAME"));
                function.setRemarks(resultSet.getString("COMMENT"));
                function.setFunctionBody(resultSet.getString("FUNCTION_DEFINITION"));
            }
            return function;
        });

    }

    public static String format(String tableName) {
        return "\"" + tableName + "\"";
    }
}
