package ai.chat2db.plugin.mysql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.SQLExecutor;
import org.springframework.util.StringUtils;

import java.sql.*;

public class MysqlDBManage extends DefaultDBManage implements DBManage {
    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, sqlBuilder, containData);
        exportViews(connection, sqlBuilder);
        exportProcedures(connection, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }
    private void exportTables(Connection connection,StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet tables = statement.executeQuery("SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'")) {
            while (tables.next()) {
                String tableName = tables.getString(1);
                exportTable(connection, tableName, sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("show create table " + tableName)) {
            if (resultSet.next()) {
                String createTableSql = "DROP TABLE IF EXISTS `" + tableName + "`;\n" +
                        resultSet.getString(2) + ";\n";
                sqlBuilder.append(createTableSql).append("\n");

                if (containData) {
                    exportTableData(connection, tableName, sqlBuilder);
                }
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
                    insertSql.append("'").append(resultSet.getString(i)).append("'");
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

    private void exportViews(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW FULL TABLES WHERE Table_type = 'VIEW'")) {
            while (resultSet.next()) {
                String viewName = resultSet.getString(1);
                exportView(connection, viewName, sqlBuilder);
            }
        }
    }

    private void exportView(Connection connection, String viewName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW CREATE VIEW " + viewName)) {
            if (resultSet.next()) {
                String createViewSql = "DROP VIEW IF EXISTS `" + viewName + "`;\n" + resultSet.getString("Create View") + ";\n";
                sqlBuilder.append(createViewSql).append("\n");
            }
        }
    }

    private void exportProcedures(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW PROCEDURE STATUS WHERE Db = DATABASE()")) {
            while (resultSet.next()) {
                String procedureName = resultSet.getString("Name");
                exportProcedure(connection, procedureName, sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String procedureName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW CREATE PROCEDURE " + procedureName)) {
            if (resultSet.next()) {
                String createProcedureSql = "DROP PROCEDURE IF EXISTS `" + procedureName + "`;\n" +
                        "delimiter ;;\n" + resultSet.getString("Create Procedure") + ";;\n" + "delimiter ;\n";
                sqlBuilder.append(createProcedureSql).append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW TRIGGERS")) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("Trigger");
                exportTrigger(connection, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SHOW CREATE TRIGGER " + triggerName)) {
            if (resultSet.next()) {
                String createTriggerSql = "DROP TRIGGER IF EXISTS `" + triggerName + "`;\n" +
                        "delimiter ;;\n" + resultSet.getString("SQL Original Statement") + ";;\n" +
                        "delimiter ;\n";
                sqlBuilder.append(createTriggerSql).append("\n");
            }
        }
    }

    @Override
    public void updateProcedure(Connection connection, String databaseName, String schemaName, Procedure procedure) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String sql = "DROP PROCEDURE " + procedure.getProcedureName();
            SQLExecutor.getInstance().execute(connection, sql, resultSet -> {});
            String procedureBody = procedure.getProcedureBody();
            SQLExecutor.getInstance().execute(connection, procedureBody, resultSet -> {});
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void connectDatabase(Connection connection, String database) {
        if (StringUtils.isEmpty(database)) {
            return;
        }
        try {
            SQLExecutor.getInstance().execute(connection,"use `" + database + "`;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE "+ format(tableName);
        SQLExecutor.getInstance().execute(connection,sql, resultSet -> null);
    }

    public static String format(String tableName) {
        return "`" + tableName + "`";
    }
}
