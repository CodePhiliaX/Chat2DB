package ai.chat2db.plugin.postgresql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static ai.chat2db.plugin.postgresql.consts.SQLConst.*;

public class PostgreSQLDBManage extends DefaultDBManage implements DBManage {

    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTypes(connection, sqlBuilder);
        exportTables(connection, databaseName, schemaName, sqlBuilder, containData);
        exportViews(connection, schemaName, sqlBuilder);
        exportFunctions(connection, schemaName, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTypes(Connection connection, StringBuilder sqlBuilder) throws SQLException {
            try (ResultSet resultSet = connection.createStatement().executeQuery(ENUM_TYPE_DDL_SQL)) {
                while (resultSet.next()) {
                    sqlBuilder.append(resultSet.getString("ddl")).append("\n");
                }
        }
    }
    private void exportTables(Connection connection, String databaseName, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, schemaName, null,
                                                                      new String[]{"TABLE", "SYSTEM TABLE","PARTITIONED TABLE"})) {
            ArrayList<String> tableNames = new ArrayList<>();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            for (String tableName : tableNames) {
                exportTable(connection, schemaName, tableName, sqlBuilder);
            }
            if (containData) {
                for (String tableName : tableNames) {
                    exportTableData(connection, schemaName, tableName, sqlBuilder);
                }
            }
        }
    }

    private void exportTable(Connection connection, String schemaName, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String sql =String.format( "select pg_get_tabledef('%s','%s',true,'COMMENTS') as ddl;", schemaName,tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("\n").append("DROP TABLE IF EXISTS ").append(tableName).append(";").append("\n")
                        .append(resultSet.getString("ddl")).append("\n");
            }
        }
    }



    private void exportViews(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {

        String sql = String.format("SELECT table_name, view_definition FROM information_schema.views WHERE table_schema = '%s'",schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String viewName = resultSet.getString("table_name");
                String viewDefinition = resultSet.getString("view_definition");
                sqlBuilder.append("CREATE OR REPLACE VIEW ").append(viewName).append(" AS ").append(viewDefinition).append("\n");
            }
        }
    }

    private void exportFunctions(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT proname, pg_get_functiondef(oid) AS function_definition FROM pg_proc " +
                                                "WHERE pronamespace = (SELECT oid FROM pg_namespace WHERE nspname = '%s')", schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String functionName = resultSet.getString("proname");
                String functionDefinition = resultSet.getString("function_definition");
                sqlBuilder.append("DROP FUNCTION IF EXISTS ").append(schemaName).append(".").append(functionName).append(";\n");
                sqlBuilder.append(functionDefinition).append(";\n\n");
            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql = "SELECT pg_get_triggerdef(oid) AS trigger_definition FROM pg_trigger";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("trigger_definition")).append(";").append("\n");
            }
        }
    }

    @Override
    public void connectDatabase(Connection connection, String database) {
        try {
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
            if (!StringUtils.isEmpty(connectInfo.getSchemaName())) {
                SQLExecutor.getInstance().execute(connection, "SET search_path TO \"" + connectInfo.getSchemaName() + "\"");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        String url = connectInfo.getUrl();
        String database = connectInfo.getDatabaseName();
        if (database != null && !database.isEmpty()) {
            url = replaceDatabaseInJdbcUrl(url, database);
        }
        connectInfo.setUrl(url);

        return super.getConnection(connectInfo);
    }


    public String replaceDatabaseInJdbcUrl(String url, String newDatabase) {
        // First split the string at the "?" character and process the query parameters
        String[] urlAndParams = url.split("\\?");
        String urlWithoutParams = urlAndParams[0];

        // Split string at "/" character in URL
        String[] parts = urlWithoutParams.split("/");

        // Take the last part, the database name, and replace it with the new database name
        parts[parts.length - 1] = newDatabase;

        // Reassemble the modified parts into a URL
        String newUrlWithoutParams = String.join("/", parts);

        // If query parameters exist, add them again
        String newUrl = urlAndParams.length > 1 ? newUrlWithoutParams + "?" + urlAndParams[1] : newUrlWithoutParams;

        return newUrl;
    }


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " + tableName;
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

}
