package ai.chat2db.plugin.sqlite;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteDBManage extends DefaultDBManage implements DBManage {

    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, databaseName, schemaName,sqlBuilder, containData);
        exportViews(connection, databaseName, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, String databaseName, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                exportTable(connection,schemaName, resultSet.getString("TABLE_NAME"), sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String schemaName, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String sql = String.format("SELECT sql FROM sqlite_master WHERE type='table' AND name='%s'", tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(format(tableName)).append(";").append("\n")
                        .append(resultSet.getString("sql")).append(";").append("\n");
                if (containData) {
                    exportTableData(connection,schemaName, tableName, sqlBuilder);
                }
            }
        }
    }

    private String format(String tableName) {
        return "\""+tableName+"\"";
    }

    private void exportViews(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"VIEW"})) {
            while (resultSet.next()) {
                exportView(connection, resultSet.getString("TABLE_NAME"), sqlBuilder);
            }
        }
    }

    private void exportView(Connection connection, String viewName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT * FROM sqlite_master WHERE type = 'view' and name='%s';", viewName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP VIEW IF EXISTS ").append(format(viewName)).append(";").append("\n")
                        .append(resultSet.getString("sql")).append(";").append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql = "SELECT * FROM sqlite_master WHERE type = 'trigger';";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("name");
                exportTrigger(connection, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT * FROM sqlite_master WHERE type = 'trigger' and name='%s';", triggerName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("sql")).append("\n");
            }
        }
    }
}
