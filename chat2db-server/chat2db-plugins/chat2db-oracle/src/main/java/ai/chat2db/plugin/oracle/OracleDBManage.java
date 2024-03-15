package ai.chat2db.plugin.oracle;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Objects;

public class OracleDBManage extends DefaultDBManage implements DBManage {
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, databaseName, sqlBuilder, containData);
        exportViews(connection, sqlBuilder, databaseName);
        exportProcedures(connection, databaseName, sqlBuilder);
        exportTriggers(connection, databaseName, sqlBuilder);
        exportFunctions(connection, databaseName, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, String databaseName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet tables = statement.executeQuery("SELECT TABLE_NAME FROM USER_TABLES")) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                exportTable(connection, databaseName, tableName, sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String databaseName, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String createTableSql = "SELECT DBMS_METADATA.GET_DDL('TABLE', table_name) FROM all_tables WHERE owner = '" + databaseName + "' AND table_name = '" + tableName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(createTableSql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE ").append(databaseName).append(".").append(tableName).append(";").append(resultSet.getString(1)).append(";").append("\n");
            }
            exportTableComments(connection, tableName, sqlBuilder);
            exportTableColumnsComments(connection, tableName, sqlBuilder);
            if (containData) {
                exportTableData(connection, tableName, sqlBuilder);
            }
        }
    }

    private void exportTableComments(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String tableCommentSql = "SELECT 'COMMENT ON TABLE ' || table_name || ' IS ''' || comments || ''';' AS table_comment_ddl FROM user_tab_comments WHERE table_name = '" + tableName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(tableCommentSql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
            }
        }
        sqlBuilder.append("\n");
    }

    private void exportTableColumnsComments(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String tableColumnsCommentSql = "SELECT 'COMMENT ON COLUMN ' || table_name || '.' || column_name || ' IS ''' || comments || ''';' AS column_comment_ddl " +
                "FROM user_col_comments " +
                "WHERE table_name = '" + tableName + "' " +
                "AND comments IS NOT NULL";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(tableColumnsCommentSql)) {
            while (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
            }
        }
        sqlBuilder.append("\n");
    }

    private void exportTableData(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                for (int i = 1; i <= columnCount; i++) {
                    String columnValue = resultSet.getString(i);
                    if (Objects.isNull(columnValue)) {
                        insertSql.append("NULL");
                    } else if (metaData.getColumnTypeName(i).equalsIgnoreCase("DATE")) {
                        // 处理日期值格式
                        columnValue = "TO_DATE('" + columnValue + "', 'YYYY-MM-DD HH24:MI:SS')";
                        insertSql.append(columnValue);
                    } else {
                        insertSql.append("'").append(columnValue).append("'");
                    }
                    if (i < columnCount) {
                        insertSql.append(", ");
                    }
                }
                insertSql.append(");\n");
                sqlBuilder.append(insertSql);
            }
        }
    }

    private void exportViews(Connection connection, StringBuilder sqlBuilder, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT VIEW_NAME FROM USER_VIEWS")) {
            while (resultSet.next()) {
                String viewName = resultSet.getString(1);
                exportView(connection, sqlBuilder, databaseName, viewName);
            }
        }
    }

    private void exportView(Connection connection, StringBuilder sqlBuilder, String databaseName, String viewName) throws SQLException {
        String createViewSql = "SELECT DBMS_METADATA.GET_DDL('VIEW', view_name)  FROM all_views WHERE owner = '" + databaseName + "'" + "AND view_name = '" + viewName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(createViewSql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append(";").append("\n");
            }
        }
    }

    private void exportProcedures(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT object_name FROM all_procedures where OWNER = '" + databaseName + "' and OBJECT_TYPE='PROCEDURE'")) {
            while (resultSet.next()) {
                String procedureName = resultSet.getString(1);
                exportProcedure(connection, databaseName, procedureName, sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String databaseName, String procedureName, StringBuilder sqlBuilder) throws SQLException {
        String createProcedureSql = "SELECT DBMS_METADATA.GET_DDL('PROCEDURE', object_name) FROM all_procedures WHERE owner = '" + databaseName + "' AND object_name = '" + procedureName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(createProcedureSql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT TRIGGER_NAME FROM all_triggers where OWNER='" + databaseName + "'")) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString(1);
                exportTrigger(connection, databaseName, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String databaseName, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        String createTriggerSql = "SELECT DBMS_METADATA.GET_DDL('TRIGGER', trigger_name) AS trigger_ddl FROM all_triggers WHERE owner = '" + databaseName + "' AND trigger_name = '" + triggerName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(createTriggerSql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append(";").append("\n");
            }
        }
    }

    private void exportFunctions(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT object_name FROM user_objects WHERE object_type = 'FUNCTION'")) {
            while (resultSet.next()) {
                String functionName = resultSet.getString(1);
                exportFunction(connection, databaseName, functionName, sqlBuilder);
            }
        }
    }

    private void exportFunction(Connection connection, String databaseName, String functionName, StringBuilder sqlBuilder) throws SQLException {
        String createFunctionSql ="SELECT DBMS_METADATA.GET_DDL('FUNCTION', object_name)  FROM all_procedures WHERE owner = '"+databaseName+"' AND object_name = '"+functionName+"'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(createFunctionSql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
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
            e.printStackTrace();
        }
    }

}
