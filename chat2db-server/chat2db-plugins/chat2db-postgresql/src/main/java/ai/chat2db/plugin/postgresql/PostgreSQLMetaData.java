package ai.chat2db.plugin.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;

import static ai.chat2db.plugin.postgresql.consts.SQLConst.FUNCTION_SQL;

public class PostgreSQLMetaData extends DefaultMetaService implements MetaData {

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        SQLExecutor.getInstance().executeSql(connection, FUNCTION_SQL.replaceFirst("tableSchema", schemaName),
            resultSet -> null);
        String ddlSql = "select showcreatetable('" + schemaName + "','" + tableName + "') as sql";
        return SQLExecutor.getInstance().executeSql(connection, ddlSql, resultSet -> {
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

    @Override
    public List<Database> databases(Connection connection) {
        return SQLExecutor.getInstance().executeSql(connection, "SELECT datname FROM pg_database;", resultSet -> {
            List<Database> databases = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    String dbName = resultSet.getString("datname");
                    if ("template0".equals(dbName) || "template1".equals(dbName)) {
                        continue;
                    }
                    Database database = new Database();
                    database.setName(dbName);
                    databases.add(database);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return databases;
        });

    }

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        return SQLExecutor.getInstance().execute(connection,
            "SELECT catalog_name, schema_name FROM information_schema.schemata;", resultSet -> {
                List<Schema> databases = new ArrayList<>();
                while (resultSet.next()) {
                    Schema schema = new Schema();
                    String name = resultSet.getString("schema_name");
                    String catalogName = resultSet.getString("catalog_name");
                    schema.setName(name);
                    schema.setDatabaseName(catalogName);
                    databases.add(schema);
                }
                return databases;
            });
    }

    private static String ROUTINES_SQL
        = " SELECT p.proname, p.prokind, pg_catalog.pg_get_functiondef(p.oid) as \"code\" FROM pg_catalog.pg_proc p "
        + "where p.prokind = '%s' and p.proname='%s';";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
        String functionName) {

        String sql = String.format(ROUTINES_SQL, "f", functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setFunctionBody(resultSet.getString("code"));
            }
            return function;
        });

    }

    private static String TRIGGER_SQL
        = "SELECT n.nspname AS \"schema\", c.relname AS \"table_name\", t.tgname AS \"trigger_name\", t.tgenabled AS "
        + "\"enabled\", pg_get_triggerdef(t.oid) AS \"trigger_body\" FROM pg_trigger t JOIN pg_class c ON c.oid = t"
        + ".tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace WHERE n.nspname = '%s' AND t.tgname ='%s';";

    private static String TRIGGER_SQL_LIST
        = "SELECT n.nspname AS \"schema\", c.relname AS \"table_name\", t.tgname AS \"trigger_name\", t.tgenabled AS "
        + "\"enabled\", pg_get_triggerdef(t.oid) AS \"trigger_body\" FROM pg_trigger t JOIN pg_class c ON c.oid = t"
        + ".tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace WHERE n.nspname = '%s';";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        String sql = String.format(TRIGGER_SQL_LIST, schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setTriggerName(resultSet.getString("trigger_name"));
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

        String sql = String.format(TRIGGER_SQL, schemaName, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("trigger_body"));
            }

            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
        String procedureName) {
        String sql = String.format(ROUTINES_SQL, "p", procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Procedure procedure = new Procedure();
            procedure.setDatabaseName(databaseName);
            procedure.setSchemaName(schemaName);
            procedure.setProcedureName(procedureName);
            if (resultSet.next()) {
                procedure.setProcedureBody(resultSet.getString("code"));
            }
            return procedure;
        });
    }

    private static String VIEW_SQL
        = "SELECT schemaname, viewname, definition FROM pg_views WHERE schemaname = '%s' AND viewname = '%s';";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, schemaName, viewName);
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
