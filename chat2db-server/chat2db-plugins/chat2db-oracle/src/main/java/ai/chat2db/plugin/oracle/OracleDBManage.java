package ai.chat2db.plugin.oracle;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.AsyncContext;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class OracleDBManage extends DefaultDBManage implements DBManage {
    private static String TABLE_DDL_SQL = "SELECT DBMS_METADATA.GET_DDL('TABLE', table_name)  as ddl FROM all_tables WHERE owner = '%s' AND table_name = '%s'";
    private static String TABLE_COMMENT_SQL = "SELECT 'COMMENT ON TABLE ' || table_name || ' IS ''' || comments || ''';' AS table_comment_ddl FROM user_tab_comments WHERE table_name = '%s'";
    private static String TABLE_COLUMN_COMMENT_SQL = "SELECT 'COMMENT ON COLUMN ' || table_name || '.' || column_name || ' IS ''' || comments || ''';' AS column_comment_ddl " +
            "FROM user_col_comments " +
            "WHERE table_name = '%s' " +
            "AND comments IS NOT NULL";
    private static String VIEW_DDL_SQL = "SELECT DBMS_METADATA.GET_DDL('VIEW', view_name) as ddl FROM all_views WHERE owner = '%s' AND view_name = '%s'";
    private String PROCEDURE_LIST_DDL ="SELECT object_name FROM all_procedures where OWNER = '%s' and OBJECT_TYPE='PROCEDURE'";
    private static String PROCEDURE_DDL_SQL = "SELECT DBMS_METADATA.GET_DDL('PROCEDURE', object_name) as ddl FROM all_procedures WHERE owner = '%s' AND object_name = '%s'";
    private static String TRIGGER_DDL_SQL = "SELECT DBMS_METADATA.GET_DDL('TRIGGER', trigger_name) AS ddl FROM all_triggers WHERE owner = '%s' AND trigger_name = '%s'";
    private static String FUNCTION_DDL_SQL = "SELECT DBMS_METADATA.GET_DDL('FUNCTION', object_name) as ddl  FROM all_procedures WHERE owner = '%s' AND object_name = '%s'";

    public void exportDatabase(Connection connection, String databaseName, String schemaName,AsyncContext asyncContext) throws SQLException {
        exportTables(connection,databaseName, schemaName, asyncContext);
        exportViews(connection, asyncContext, schemaName);
        exportProcedures(connection, schemaName, asyncContext);
        exportTriggers(connection, schemaName, asyncContext);
        exportFunctions(connection, schemaName, asyncContext);
    }

    private void exportTables(Connection connection, String databaseName, String schemaName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, schemaName, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                exportTable(connection,databaseName, schemaName, tableName, asyncContext);
            }
        }
    }


    private void exportTable(Connection connection, String databaseName, String schemaName, String tableName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(TABLE_DDL_SQL, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP TABLE ").append(schemaName).append(".").append(tableName).append(";")
                        .append(resultSet.getString("ddl")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
            exportTableComments(connection, tableName, asyncContext);
            exportTableColumnsComments(connection, tableName, asyncContext);
            if (asyncContext.isContainsData()) {
                exportTableData(connection,databaseName,schemaName, tableName, asyncContext);
            }
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

    private void exportView(Connection connection, AsyncContext asyncContext, String schemaName, String viewName) throws SQLException {
        String sql = String.format(VIEW_DDL_SQL, schemaName, viewName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("ddl")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportProcedures(Connection connection, String schemaName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(PROCEDURE_LIST_DDL,schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql))  {
            while (resultSet.next()) {
                String procedureName = resultSet.getString("object_name");
                exportProcedure(connection, schemaName, procedureName, asyncContext);
            }
        }
    }

    private void exportProcedure(Connection connection, String schemaName, String procedureName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(PROCEDURE_DDL_SQL, schemaName, procedureName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("ddl")).append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
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

    private void exportTrigger(Connection connection, String schemaName, String triggerName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(TRIGGER_DDL_SQL, schemaName, triggerName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("ddl")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportFunctions(Connection connection, String schemaName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getFunctions(null, schemaName, null)) {
            while (resultSet.next()) {
                String functionName = resultSet.getString("FUNCTION_NAME");
                exportFunction(connection, schemaName, functionName, asyncContext);
            }
        }
    }

    private void exportFunction(Connection connection, String schemaName, String functionName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format(FUNCTION_DDL_SQL, schemaName, functionName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append(resultSet.getString("ddl")).append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
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

}
