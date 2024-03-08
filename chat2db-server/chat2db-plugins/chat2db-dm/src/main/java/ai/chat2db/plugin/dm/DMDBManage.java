package ai.chat2db.plugin.dm;

import java.sql.*;
import java.util.Objects;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class DMDBManage extends DefaultDBManage implements DBManage {
    private String format(String tableName) {
        return "\"" + tableName + "\"";
    }
    private static String ROUTINES_SQL
            = "SELECT OWNER, NAME, TEXT FROM ALL_SOURCE WHERE TYPE = '%s' AND OWNER = '%s' AND NAME = '%s' ORDER BY LINE";
    private static String TRIGGER_SQL_LIST = "SELECT OWNER, TRIGGER_NAME FROM ALL_TRIGGERS WHERE OWNER = '%s'";

    private static String TRIGGER_SQL
            = "SELECT OWNER, TRIGGER_NAME, TABLE_OWNER, TABLE_NAME, TRIGGERING_TYPE, TRIGGERING_EVENT, STATUS, TRIGGER_BODY "
            + "FROM ALL_TRIGGERS WHERE OWNER = '%s' AND TRIGGER_NAME = '%s'";

    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, sqlBuilder, schemaName, containData);
        exportViews(connection, schemaName, sqlBuilder);
        exportProcedures(connection, schemaName, sqlBuilder);
        exportTriggers(connection,schemaName, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, StringBuilder sqlBuilder, String schemaName, boolean containData) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet tables = statement.executeQuery("SELECT TABLE_NAME FROM ALL_TABLES where OWNER='" + schemaName + "' and TABLESPACE_NAME='MAIN'")) {
            while (tables.next()) {
                String tableName = tables.getString(1);
                exportTable(connection, tableName, schemaName, sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String sql = """
                     SELECT
                         (SELECT comments FROM user_tab_comments WHERE table_name = '%s') AS comments,
                         (SELECT dbms_metadata.get_ddl('TABLE', '%s', '%s') FROM dual) AS ddl
                     FROM dual;
                     """;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(String.format(sql, tableName, tableName, schemaName))) {
            String formatSchemaName = format(schemaName);
            String formatTableName = format(tableName);
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(formatSchemaName).append(".").append(formatTableName).append(";\n")
                        .append(resultSet.getString(2)).append("\n");
                String comment = resultSet.getString(1);
                if (StringUtils.isNotBlank(comment)) {
                    sqlBuilder.append("COMMENT ON TABLE ").append(formatSchemaName).append(".").append(formatTableName)
                            .append(" IS ").append("'").append(comment).append("';");
                }
            }
            if (containData) {
                exportTableData(connection, tableName, sqlBuilder);
            }
        }
    }


    private void exportTableData(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        StringBuilder insertSql = new StringBuilder();
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("select * from " + tableName)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                insertSql.append("INSERT INTO ").append(tableName).append(" VALUES (");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = resultSet.getString(i);
                    if (Objects.isNull(value)) {
                        insertSql.append("NULL");
                    } else {

                        insertSql.append("'").append(value).append("'");
                    }
                    if (i < metaData.getColumnCount()) {
                        insertSql.append(", ");
                    }
                }
                insertSql.append(");\n");
            }
            insertSql.append("\n");
        }
        sqlBuilder.append(insertSql);
    }

    private void exportViews(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet resultSet = metadata.getTables(null, schemaName, null, new String[]{"VIEW"})) {
            while (resultSet.next()) {
                String viewName = resultSet.getString("TABLE_NAME");
                exportView(connection, viewName, schemaName, sqlBuilder);
            }
        }
    }

    private void exportView(Connection connection, String viewName, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT DBMS_METADATA.GET_DDL('VIEW','" + viewName + "','" + schemaName + "') FROM DUAL;")) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
            }
        }
    }

    private void exportProcedures(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getProcedures(null, schemaName, null)) {
            while (resultSet.next()) {
                String procedureName = resultSet.getString("PROCEDURE_NAME");
                exportProcedure(connection, schemaName,procedureName, sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String schemaName, String procedureName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format(ROUTINES_SQL,"PROC", schemaName,procedureName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("TEXT")).append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql =String.format(TRIGGER_SQL_LIST, schemaName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("TRIGGER_NAME");
                exportTrigger(connection,schemaName, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String schemaName, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format(TRIGGER_SQL, schemaName,triggerName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("TRIGGER_BODY")).append("\n");
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
            SQLExecutor.getInstance().execute(connection, "SET SCHEMA \"" + schemaName + "\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE IF EXISTS " +tableName;
        SQLExecutor.getInstance().execute(connection,sql, resultSet -> null);
    }
}
