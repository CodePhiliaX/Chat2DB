package ai.chat2db.plugin.oracle;

import java.sql.Connection;
import java.sql.SQLException;
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

public class OracleMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "select dbms_metadata.get_ddl('TABLE','" + tableName + "') as sql from dual,"
            + "user_tables where table_name = '" + tableName + "'";
        return SQLExecutor.getInstance().executeSql(connection, sql, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("sql");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }

    private static String ROUTINES_SQL
        = "SELECT LINE, TEXT "
        + "FROM ALL_SOURCE "
        + "WHERE TYPE = '%s' AND NAME = '%s' "
        + "ORDER BY LINE";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
        String functionName) {

        String sql = String.format(ROUTINES_SQL, "FUNCTION", functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                sb.append(resultSet.getString("TEXT") + "\n");
            }
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            function.setFunctionBody(sb.toString());
            return function;

        });

    }

    private static String TRIGGER_SQL_LIST
        = "SELECT TRIGGER_NAME "
        + "FROM ALL_TRIGGERS WHERE OWNER = '%s'";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, String.format(TRIGGER_SQL_LIST, schemaName),
            resultSet -> {
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

        String sql = String.format(ROUTINES_SQL, "TRIGGER", triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                sb.append(resultSet.getString("TEXT") + "\n");
            }
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            trigger.setTriggerBody(resultSet.getString(sb.toString()));
            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
        String procedureName) {
        String sql = String.format(ROUTINES_SQL, "PROCEDURE", procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                sb.append(resultSet.getString("TEXT") + "\n");
            }
            Procedure procedure = new Procedure();
            procedure.setDatabaseName(databaseName);
            procedure.setSchemaName(schemaName);
            procedure.setProcedureName(procedureName);
            procedure.setProcedureBody(sb.toString());
            return procedure;
        });
    }

    private static String VIEW_SQL
        = "SELECT VIEW_NAME, TEXT FROM ALL_VIEWS WHERE OWNER = '%s' AND VIEW_NAME = '%s'";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("TEXT"));
            }
            return table;
        });
    }
}
