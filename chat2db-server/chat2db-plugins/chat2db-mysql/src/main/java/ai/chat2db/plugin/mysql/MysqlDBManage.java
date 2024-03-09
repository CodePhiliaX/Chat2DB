package ai.chat2db.plugin.mysql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.SQLExecutor;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.Objects;

public class MysqlDBManage extends DefaultDBManage implements DBManage {
    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, databaseName, sqlBuilder, containData);
        exportViews(connection, databaseName, sqlBuilder);
        exportProcedures(connection, databaseName, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, String databaseName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, null, null, new String[]{"TABLE", "SYSTEM TABLE"})) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                exportTable(connection, tableName, sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String TABLE_DDL_SQL = "show create table %s ";
        String sql = String.format(TABLE_DDL_SQL, tableName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(format(tableName)).append(";").append("\n")
                        .append(resultSet.getString("Create Table")).append(";").append("\n");
                if (containData) {
                    exportTableData(connection, tableName, sqlBuilder);
                }
            }
        }
    }

    private void exportTableData(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String TABLE_QUERY_SQL = "select * from %s";
        String sql = String.format(TABLE_QUERY_SQL, tableName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
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
                String viewName = resultSet.getString("TABLE_NAME");
                exportView(connection, viewName, sqlBuilder);
            }
        }
    }

    private void exportView(Connection connection, String viewName, StringBuilder sqlBuilder) throws SQLException {
        String VIEW_DDL_SQL = "show create view %s ";
        String sql = String.format(VIEW_DDL_SQL, viewName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP VIEW IF EXISTS ").append(format(viewName)).append(";").append("\n")
                        .append(resultSet.getString("Create View")).append(";").append("\n");
            }
        }
    }

    private void exportProcedures(Connection connection, String databaseName, StringBuilder sqlBuilder) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getProcedures(databaseName, null, null)) {
            while (resultSet.next()) {
                String procedureName = resultSet.getString("PROCEDURE_NAME");
                exportProcedure(connection, procedureName, sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String procedureName, StringBuilder sqlBuilder) throws SQLException {
        String PROCEDURE_DDL_SQL = "show create procedure %s ";
        String sql = String.format(PROCEDURE_DDL_SQL, procedureName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP PROCEDURE IF EXISTS ").append(format(procedureName)).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("Create Procedure")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql ="SHOW TRIGGERS";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("Trigger");
                exportTrigger(connection, triggerName, sqlBuilder);
            }
        }
    }

    private void exportTrigger(Connection connection, String triggerName, StringBuilder sqlBuilder) throws SQLException {
        String TRIGGER_DDL_SQL = "show create trigger %s ";
        String sql = String.format(TRIGGER_DDL_SQL, triggerName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TRIGGER IF EXISTS ").append(format(triggerName)).append(";").append("\n")
                        .append("delimiter ;;").append("\n").append(resultSet.getString("SQL Original Statement")).append(";;")
                        .append("\n").append("delimiter ;").append("\n");
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
