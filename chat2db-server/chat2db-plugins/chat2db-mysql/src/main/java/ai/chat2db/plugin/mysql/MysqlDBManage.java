package ai.chat2db.plugin.mysql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.AsyncContext;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.SQLExecutor;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.Objects;

public class MysqlDBManage extends DefaultDBManage implements DBManage {
    @Override
    public void exportDatabase(Connection connection, String databaseName, String schemaName, AsyncContext asyncContext) throws SQLException {
        exportTables(connection, databaseName, asyncContext);
        exportViews(connection, databaseName, asyncContext);
        exportProcedures(connection, asyncContext);
        exportTriggers(connection, asyncContext);
        exportFunctions(connection, databaseName, asyncContext);
    }

    private void exportFunctions(Connection connection, String databaseName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getFunctions(databaseName, null, null)) {
            while (resultSet.next()) {
                exportFunction(connection, resultSet.getString("FUNCTION_NAME"), asyncContext);
            }

        }
    }

    private void exportFunction(Connection connection, String functionName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("SHOW CREATE FUNCTION %s;", functionName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP FUNCTION IF EXISTS ").append(functionName).append(";").append("\n")
                          .append(resultSet.getString("Create Function")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportTables(Connection connection, String databaseName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                exportTable(connection, resultSet.getString("TABLE_NAME"), asyncContext);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("show create table %s ", tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(format(tableName)).append(";").append("\n")
                        .append(resultSet.getString("Create Table")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
                if (asyncContext.isContainsData()) {
                    exportTableData(connection, null,tableName, asyncContext);
                }
            }
        }
    }


    private void exportViews(Connection connection, String databaseName, AsyncContext asyncContext) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"VIEW"})) {
            while (resultSet.next()) {
                exportView(connection, resultSet.getString("TABLE_NAME"), asyncContext);
            }
        }
    }

    private void exportView(Connection connection, String viewName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("show create view %s ", viewName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP VIEW IF EXISTS ").append(format(viewName)).append(";").append("\n")
                        .append(resultSet.getString("Create View")).append(";").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportProcedures(Connection connection, AsyncContext asyncContext) throws SQLException {
        String sql = "SHOW PROCEDURE STATUS WHERE Db = DATABASE()";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                exportProcedure(connection, resultSet.getString("Name"), asyncContext);
            }
        }
    }

    private void exportProcedure(Connection connection, String procedureName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("show create procedure %s ", procedureName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP PROCEDURE IF EXISTS ").append(format(procedureName)).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("Create Procedure")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
                asyncContext.write(sqlBuilder.toString());
            }
        }
    }

    private void exportTriggers(Connection connection, AsyncContext asyncContext) throws SQLException {
        String sql = "SHOW TRIGGERS";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("Trigger");
                exportTrigger(connection, triggerName, asyncContext);
            }
        }
    }

    private void exportTrigger(Connection connection, String triggerName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("show create trigger %s ", triggerName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("DROP TRIGGER IF EXISTS ").append(format(triggerName)).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("SQL Original Statement")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
                asyncContext.write(sqlBuilder.toString());
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
        }finally {
            connection.setAutoCommit(true);
        }

    }

    @Override
    public void connectDatabase(Connection connection, String database) {
        if (StringUtils.isEmpty(database)) {
            return;
        }
        try {
            SQLExecutor.getInstance().execute(connection, "use `" + database + "`;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " + format(tableName);
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

    public static String format(String tableName) {
        return "`" + tableName + "`";
    }
}
