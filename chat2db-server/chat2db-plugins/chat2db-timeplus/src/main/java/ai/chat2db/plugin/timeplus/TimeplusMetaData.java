package ai.chat2db.plugin.timeplus;

import static ai.chat2db.spi.util.SortUtils.sortDatabase;

import ai.chat2db.plugin.timeplus.builder.TimeplusSqlBuilder;
import ai.chat2db.plugin.timeplus.type.TimeplusColumnTypeEnum;
import ai.chat2db.plugin.timeplus.type.TimeplusEngineTypeEnum;
import ai.chat2db.plugin.timeplus.type.TimeplusIndexTypeEnum;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class TimeplusMetaData extends DefaultMetaService implements MetaData {

    private static String ROUTINES_SQL = "";
    private static String TRIGGER_SQL = "";
    private static String TRIGGER_SQL_LIST = "";
    private static String SELECT_TABLE_COLUMNS =
        "select * from `system`.columns where table ='%s' and database='%s';";
    private static String VIEW_SQL =
        "SELECT create_table_query from system.`tables` WHERE `database`='%s' and name='%s'";
    private List<String> systemDatabases = Arrays.asList(
        "information_schema",
        "system"
    );
    public static final String FUNCTION_SQL =
        "SELECT name,create_query as ddl from system.functions where origin='ExecutableUserDefined'";

    public static String format(String tableName) {
        return "`" + tableName + "`";
    }

    @Override
    public List<Function> functions(
        Connection connection,
        String databaseName,
        String schemaName
    ) {
        return SQLExecutor.getInstance()
            .execute(connection, FUNCTION_SQL, resultSet -> {
                List<Function> functions = new ArrayList<>();
                while (resultSet.next()) {
                    Function function = new Function();
                    function.setFunctionName(resultSet.getString("name"));
                    functions.add(function);
                }
                return functions;
            });
    }

    @Override
    public List<Database> databases(Connection connection) {
        List<Database> list = SQLExecutor.getInstance()
            .execute(
                connection,
                "SELECT name FROM system.databases;",
                resultSet -> {
                    List<Database> databases = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            String dbName = resultSet.getString("name");
                            Database database = new Database();
                            database.setName(dbName);
                            databases.add(database);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return databases;
                }
            );
        return sortDatabase(list, systemDatabases, connection);
    }

    @Override
    public String tableDDL(
        Connection connection,
        @NotEmpty String databaseName,
        String schemaName,
        @NotEmpty String tableName
    ) {
        String sql =
            "SHOW CREATE " + format(schemaName) + "." + format(tableName);
        return SQLExecutor.getInstance()
            .execute(connection, sql, resultSet -> {
                if (resultSet.next()) {
                    return resultSet.getString("statement");
                }
                return null;
            });
    }

    @Override
    public Function function(
        Connection connection,
        @NotEmpty String databaseName,
        String schemaName,
        String functionName
    ) {
        return SQLExecutor.getInstance()
            .execute(connection, FUNCTION_SQL, resultSet -> {
                Function function = new Function();
                function.setDatabaseName(databaseName);
                function.setSchemaName(schemaName);
                function.setFunctionName(functionName);
                if (resultSet.next()) {
                    /*                function.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                function.setRemarks(resultSet.getString("ROUTINE_COMMENT"));*/
                    function.setFunctionBody(resultSet.getString("ddl"));
                }
                return function;
            });
    }

    @Override
    public List<Trigger> triggers(
        Connection connection,
        String databaseName,
        String schemaName
    ) {
        List<Trigger> triggers = new ArrayList<>();
        return triggers;
    }

    @Override
    public Trigger trigger(
        Connection connection,
        @NotEmpty String databaseName,
        String schemaName,
        String triggerName
    ) {
        String sql = String.format(TRIGGER_SQL, databaseName, triggerName);
        return SQLExecutor.getInstance()
            .execute(connection, sql, resultSet -> {
                Trigger trigger = new Trigger();
                trigger.setDatabaseName(databaseName);
                trigger.setSchemaName(schemaName);
                trigger.setTriggerName(triggerName);
                if (resultSet.next()) {
                    trigger.setTriggerBody(
                        resultSet.getString("ACTION_STATEMENT")
                    );
                }
                return trigger;
            });
    }

    @Override
    public Procedure procedure(
        Connection connection,
        @NotEmpty String databaseName,
        String schemaName,
        String procedureName
    ) {
        String sql = String.format(
            ROUTINES_SQL,
            "PROCEDURE",
            schemaName,
            procedureName
        );
        return SQLExecutor.getInstance()
            .execute(connection, sql, resultSet -> {
                Procedure procedure = new Procedure();
                procedure.setDatabaseName(databaseName);
                procedure.setSchemaName(schemaName);
                procedure.setProcedureName(procedureName);
                if (resultSet.next()) {
                    procedure.setSpecificName(
                        resultSet.getString("SPECIFIC_NAME")
                    );
                    procedure.setRemarks(
                        resultSet.getString("ROUTINE_COMMENT")
                    );
                    procedure.setProcedureBody(
                        resultSet.getString("ROUTINE_DEFINITION")
                    );
                }
                return procedure;
            });
    }

    @Override
    public List<TableColumn> columns(
        Connection connection,
        String databaseName,
        String schemaName,
        String tableName
    ) {
        final String db = "default";
        String sql = String.format(SELECT_TABLE_COLUMNS, tableName, db);
        List<TableColumn> tableColumns = new ArrayList<>();

        return SQLExecutor.getInstance()
            .execute(connection, sql, resultSet -> {
                while (resultSet.next()) {
                    TableColumn column = new TableColumn();
                    column.setDatabaseName(db);
                    column.setTableName(tableName);
                    column.setOldName(resultSet.getString("name"));
                    column.setName(resultSet.getString("name"));
                    String dataType = resultSet.getString("type");
                    if (dataType.startsWith("nullable(")) {
                        dataType = dataType.substring(9, dataType.length() - 1);
                        column.setNullable(1);
                    }
                    column.setColumnType(dataType);
                    column.setDefaultValue(
                        resultSet.getString("default_expression")
                    );
                    //                column.setAutoIncrement(resultSet.getString("EXTRA").contains("auto_increment"));
                    column.setComment(resultSet.getString("comment"));
                    column.setOrdinalPosition(resultSet.getInt("position"));
                    column.setDecimalDigits(resultSet.getInt("numeric_scale"));
                    /*column.setCharSetName(resultSet.getString("CHARACTER_SET_NAME"));
                column.setCollationName(resultSet.getString("COLLATION_NAME"));*/
                    setColumnSize(column, dataType);
                    tableColumns.add(column);
                }
                return tableColumns;
            });
    }

    private void setColumnSize(TableColumn column, String columnType) {
        try {
            if (columnType.contains("(")) {
                String size = columnType.substring(
                    columnType.indexOf("(") + 1,
                    columnType.indexOf(")")
                ); //"size" can be a number or "3, 'UTC'" with timezone for datetime objects
                if (
                    "SET".equalsIgnoreCase(column.getColumnType()) ||
                    "ENUM".equalsIgnoreCase(column.getColumnType())
                ) {
                    column.setValue(size);
                } else {
                    if (size.contains(",")) {
                        String[] sizes = size.split(",");
                        if (StringUtils.isNotBlank(sizes[0])) {
                            column.setColumnSize(Integer.parseInt(sizes[0]));
                        }
                        if (StringUtils.isNotBlank(sizes[1])) {
                            //can be " 'UTC'"
                            if (sizes[1].contains("'") == false) {
                                column.setDecimalDigits(
                                    Integer.parseInt(sizes[1])
                                );
                            }
                        }
                    } else {
                        column.setColumnSize(Integer.parseInt(size));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Table view(
        Connection connection,
        String databaseName,
        String schemaName,
        String viewName
    ) {
        final String db = "default";
        String sql = String.format(VIEW_SQL, db, viewName);
        return SQLExecutor.getInstance()
            .execute(connection, sql, resultSet -> {
                Table table = new Table();
                table.setDatabaseName(db);
                table.setSchemaName(schemaName);
                table.setName(viewName);
                if (resultSet.next()) {
                    table.setDdl(resultSet.getString("create_table_query"));
                }
                return table;
            });
    }

    @Override
    public List<TableIndex> indexes(
        Connection connection,
        String databaseName,
        String schemaName,
        String tableName
    ) {
        List<TableIndex> rv = new ArrayList<>();
        return rv;
    }

    private List<TableIndexColumn> getTableIndexColumn(ResultSet resultSet)
        throws SQLException {
        List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
        return tableIndexColumns;
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new TimeplusSqlBuilder();
    }

    @Override
    public TableMeta getTableMeta(
        String databaseName,
        String schemaName,
        String tableName
    ) {
        return TableMeta.builder()
            .columnTypes(TimeplusColumnTypeEnum.getTypes())
            .engineTypes(TimeplusEngineTypeEnum.getTypes())
            //.indexTypes(TimeplusIndexTypeEnum.getIndexTypes())
            .build();
    }

    @Override
    public String getMetaDataName(String... names) {
        //avoid default.default.abc
        String rv = Arrays.stream(names)
            .filter(name -> StringUtils.isNotBlank(name))
            .map(name -> "`" + name + "`")
            .collect(Collectors.joining("."));
        rv = rv.replaceFirst("`default`.`default`", "`default`");
        return rv;
    }

    @Override
    public List<String> getSystemDatabases() {
        return systemDatabases;
    }
}
