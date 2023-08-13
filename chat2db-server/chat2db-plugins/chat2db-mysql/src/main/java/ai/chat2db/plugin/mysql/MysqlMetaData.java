package ai.chat2db.plugin.mysql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;

public class MysqlMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName) {
        String sql = "SHOW CREATE TABLE " + format(databaseName) + "."
            + format(tableName);
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

        String sql = String.format(ROUTINES_SQL, "FUNCTION", databaseName, functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                function.setRemarks(resultSet.getString("ROUTINE_COMMENT"));
                function.setFunctionBody(resultSet.getString("ROUTINE_DEFINITION"));
            }
            return function;
        });

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
                trigger.setTriggerName(resultSet.getString("TRIGGER_NAME"));
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

        String sql = String.format(TRIGGER_SQL, databaseName, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("ACTION_STATEMENT"));
            }
            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
        String procedureName) {
        String sql = String.format(ROUTINES_SQL, "PROCEDURE", databaseName, procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Procedure procedure = new Procedure();
            procedure.setDatabaseName(databaseName);
            procedure.setSchemaName(schemaName);
            procedure.setProcedureName(procedureName);
            if (resultSet.next()) {
                procedure.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                procedure.setRemarks(resultSet.getString("ROUTINE_COMMENT"));
                procedure.setProcedureBody(resultSet.getString("ROUTINE_DEFINITION"));
            }
            return procedure;
        });
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
}
