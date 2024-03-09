package ai.chat2db.plugin.h2;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2DBManage extends DefaultDBManage implements DBManage {
    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTablesAndViews(connection, schemaName, sqlBuilder, containData);
//        exportProcedures(connection, sqlBuilder);
//        exportTriggers(connection, sqlBuilder);
//        exportFunctions(connection, databaseName, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportFunctions(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getFunctions(databaseName, null, null)) {
            while (resultSet.next()) {
                exportFunction(connection, resultSet.getString("FUNCTION_NAME"), sqlBuilder);
            }

        }
    }

    private void exportFunction(Connection connection, String functionName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SHOW CREATE FUNCTION %s;", functionName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP FUNCTION IF EXISTS ").append(functionName).append(";").append("\n")
                        .append(resultSet.getString("Create Function")).append(";").append("\n");
            }
        }
    }

    private void exportTablesAndViews(Connection connection, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String sql = String.format("SCRIPT NODATA NOPASSWORDS NOSETTINGS DROP SCHEMA %s;", schemaName);
        if (containData) {
            sql = sql.replace("NODATA", "");
        }
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String script = resultSet.getString("SCRIPT");
                if (!(script.startsWith("CREATE USER")||script.startsWith("--"))) {
                    sqlBuilder.append(script);
                    sqlBuilder.append("\n");
                }
            }
        }

    }


    private void exportProcedures(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql = "SHOW PROCEDURE STATUS WHERE Db = DATABASE()";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                exportProcedure(connection, resultSet.getString("Name"), sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String procedureName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("show create procedure %s ", procedureName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP PROCEDURE IF EXISTS ").append(procedureName).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("Create Procedure")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql = "SHOW TRIGGERS";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("Trigger");
                exportTrigger(connection, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("show create trigger %s ", triggerName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TRIGGER IF EXISTS ").append(triggerName).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("SQL Original Statement")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
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

        }
    }


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " + tableName;
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }
}
