package ai.chat2db.plugin.oracle;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class OracleDBManage extends DefaultDBManage implements DBManage {
    private static String TABLE_COMMENT_SQL = "SELECT 'COMMENT ON TABLE ' || table_name || ' IS ''' || comments || ''';' AS table_comment_ddl FROM user_tab_comments WHERE table_name = '%s'";
    private static String TABLE_COLUMN_COMMENT_SQL = "SELECT 'COMMENT ON COLUMN ' || table_name || '.' || column_name || ' IS ''' || comments || ''';' AS column_comment_ddl " +
            "FROM user_col_comments " +
            "WHERE table_name = '%s' " +
            "AND comments IS NOT NULL";
    public void exportDatabase(Connection connection, String databaseName, String schemaName, AsyncContext asyncContext) throws SQLException {
        exportTables(connection, databaseName, schemaName, asyncContext);
        exportViews(connection, asyncContext, schemaName);
        exportProcedures(connection, schemaName, asyncContext);
        exportTriggers(connection, schemaName, asyncContext);
        exportFunctions(connection, schemaName, asyncContext);
    }

    private void exportTables(Connection connection, String databaseName, String schemaName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, schemaName, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                exportTable(connection, databaseName, schemaName, tableName, asyncContext);
            }
        }
    }


    public void exportTable(Connection connection, String databaseName, String schemaName, String tableName, AsyncContext asyncContext) throws SQLException {
        String tableDDL = Chat2DBContext.getMetaData().tableDDL(connection, databaseName, schemaName, tableName);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("DROP TABLE ").append(SqlUtils.quoteObjectName(tableName)).append(";")
                .append(tableDDL).append(";").append("\n");
        asyncContext.write(sqlBuilder.toString());

        exportTableComments(connection, tableName, asyncContext);
        exportTableColumnsComments(connection, tableName, asyncContext);
        if (asyncContext.isContainsData()) {
            exportTableData(connection, databaseName, schemaName, tableName, asyncContext);
        }

    }

    private void exportTableComments(Connection connection, String tableName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(TABLE_COMMENT_SQL, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("table_comment_ddl")).append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportTableColumnsComments(Connection connection, String tableName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(TABLE_COLUMN_COMMENT_SQL, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("column_comment_ddl")).append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportViews(Connection connection, AsyncContext asyncContext, String schemaName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, schemaName, null, new String[]{"VIEW"})) {
            while (resultSet.next()) {
                String viewName = resultSet.getString("TABLE_NAME");
                exportView(connection, asyncContext, schemaName, viewName);
            }
        }
    }

    private void exportView(Connection connection, AsyncContext asyncContext, String schemaName, String viewName) {
        Table view = Chat2DBContext.getMetaData().view(connection, null, schemaName, viewName);
        asyncContext.write(view.getDdl() + ";" + "\n");
    }

    private void exportProcedures(Connection connection, String schemaName, AsyncContext asyncContext) {
        List<Procedure> procedures = Chat2DBContext.getMetaData().procedures(connection, null, schemaName);
        if (CollectionUtils.isNotEmpty(procedures)) {
            for (Procedure procedure : procedures) {
                String procedureName = procedure.getProcedureName();
                exportProcedure(connection, schemaName, procedureName, asyncContext);
            }
        }

    }

    private void exportProcedure(Connection connection, String schemaName, String procedureName, AsyncContext asyncContext) {
        Procedure procedure = Chat2DBContext.getMetaData().procedure(connection, null, schemaName, procedureName);
        asyncContext.write(procedure.getProcedureBody() + "\n");

    }

    private void exportTriggers(Connection connection, String schemaName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("SELECT TRIGGER_NAME FROM all_triggers where OWNER='%s'", schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("TRIGGER_NAME");
                exportTrigger(connection, schemaName, triggerName, asyncContext);
            }
        }
    }

    private void exportTrigger(Connection connection, String schemaName, String triggerName, AsyncContext asyncContext) {
        Trigger trigger = Chat2DBContext.getMetaData().trigger(connection, null, schemaName, triggerName);
        asyncContext.write(trigger.getTriggerBody() + ";" + "\n");

    }

    private void exportFunctions(Connection connection, String schemaName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getFunctions(null, schemaName, null)) {
            while (resultSet.next()) {
                String functionName = resultSet.getString("FUNCTION_NAME");
                exportFunction(connection, schemaName, functionName, asyncContext);
            }
        }
    }

    private void exportFunction(Connection connection, String schemaName, String functionName, AsyncContext asyncContext) {
        Function function = Chat2DBContext.getMetaData().function(connection, null, schemaName, functionName);
        asyncContext.write(function.getFunctionBody() + "\n");
    }


    @Override
    public void connectDatabase(Connection connection, String database) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (ObjectUtils.anyNull(connectInfo) || StringUtils.isEmpty(connectInfo.getSchemaName())) {
            return;
        }
        String schemaName = connectInfo.getSchemaName();
        try {
            SQLExecutor.getInstance().execute(connection, "ALTER SESSION SET CURRENT_SCHEMA = \"" + schemaName + "\"");
        } catch (SQLException e) {
            log.error("connectDatabase error", e);
        }
    }

    @Override
    public void copyTable(Connection connection, String databaseName, String schemaName, String tableName, String newTableName, boolean copyData) throws SQLException {
        String sql = "";
        if (copyData) {
            sql = "CREATE TABLE " + newTableName + " AS SELECT * FROM " + tableName;
        } else {
            sql = "CREATE TABLE " + newTableName + " AS SELECT * FROM " + tableName + " WHERE 1=0";
        }
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

}
