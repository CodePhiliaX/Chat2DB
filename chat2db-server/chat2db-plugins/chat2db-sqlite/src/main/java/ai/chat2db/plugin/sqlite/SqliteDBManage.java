package ai.chat2db.plugin.sqlite;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

public class SqliteDBManage extends DefaultDBManage implements DBManage {


    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, databaseName, sqlBuilder, containData);
        exportViews(connection, databaseName, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, String databaseName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                exportTable(connection, resultSet.getString("TABLE_NAME"), sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String sql = String.format("SELECT sql FROM sqlite_master WHERE type='table' AND name='%s'", tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(format(tableName)).append(";").append("\n")
                        .append(resultSet.getString("sql")).append(";").append("\n");
                if (containData) {
                    exportTableData(connection, tableName, sqlBuilder);
                }
            }
        }
    }

    private String format(String tableName) {
        return "\""+tableName+"\"";
    }


    private void exportTableData(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("select * from %s", tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                sqlBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = resultSet.getString(i);
                    if (Objects.isNull(value)) {
                        sqlBuilder.append("NULL");
                    } else {
                        sqlBuilder.append("'").append(value).append("'");
                    }
                    if (i < metaData.getColumnCount()) {
                        sqlBuilder.append(", ");
                    }
                }
                sqlBuilder.append(");\n");
            }
            sqlBuilder.append("\n");
        }
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
