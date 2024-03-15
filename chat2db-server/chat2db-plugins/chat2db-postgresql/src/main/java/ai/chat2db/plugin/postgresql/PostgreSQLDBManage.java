package ai.chat2db.plugin.postgresql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;

import static ai.chat2db.plugin.postgresql.consts.SQLConst.*;

public class PostgreSQLDBManage extends DefaultDBManage implements DBManage {


    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTypes(connection, schemaName, sqlBuilder);
        exportTables(connection, schemaName, sqlBuilder, containData);
        exportViews(connection, schemaName, sqlBuilder);
        exportFunctions(connection, schemaName, sqlBuilder);
        exportTriggers(connection, schemaName, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTypes(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
            try (Statement statement = connection.createStatement(); ResultSet ddl = statement.executeQuery(ENUM_TYPE_DDL_SQL)) {
                while (ddl.next()) {
                    sqlBuilder.append(ddl.getString(1)).append("\n");
                }
        }
    }
    private void exportTables(Connection connection, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String tablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + schemaName + "' AND table_type = 'BASE TABLE'";
        try (Statement statement = connection.createStatement(); ResultSet tables = statement.executeQuery(tablesQuery)) {
            while (tables.next()) {
                String tableName = tables.getString(1);
                exportTable(connection, schemaName, tableName, sqlBuilder, containData);
            }
        }
    }

    private void exportTable(Connection connection, String schemaName, String tableName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String tableQuery = "select pg_get_tabledef" + "(" + "'" + schemaName + "'" + "," + "'" + tableName + "'" + "," + "true" + "," + "'" + "COMMENTS" + "'" + ")" + ";";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(tableQuery)) {
            sqlBuilder.append("\n").append("DROP TABLE IF EXISTS ").append(schemaName).append(".").append(tableName).append(";\n");
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString(1)).append("\n");
            }
            if (containData) {
                exportTableData(connection, schemaName, tableName, sqlBuilder);
            }
        }
    }

    private void exportTableData(Connection connection, String schemaName, String tableName, StringBuilder sqlBuilder) throws SQLException {
        StringBuilder insertSql = new StringBuilder();
        String dataQuery = "SELECT * FROM " + schemaName + "." + tableName;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(dataQuery)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                insertSql.append("INSERT INTO ").append(tableName).append(" VALUES (");
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    if (value != null) {
                        insertSql.append("'").append(value).append("'");
                    } else {
                        insertSql.append("NULL");
                    }
                    if (i < columnCount) {
                        insertSql.append(", ");
                    }
                }
                insertSql.append(");\n");
            }
            insertSql.append("\n");
            sqlBuilder.append(insertSql);
        }
    }


    private void exportViews(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String viewsQuery = "SELECT table_name, view_definition FROM information_schema.views WHERE table_schema = '" + schemaName + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(viewsQuery)) {
            while (resultSet.next()) {
                String viewName = resultSet.getString("table_name");
                String viewDefinition = resultSet.getString("view_definition");
                sqlBuilder.append("DROP VIEW IF EXISTS ").append(schemaName).append(".").append(viewName).append(";\n");
                sqlBuilder.append("CREATE VIEW ").append(schemaName).append(".").append(viewName).append(" AS ").append(viewDefinition).append(";\n\n");
            }
        }
    }

    private void exportFunctions(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String functionsQuery = "SELECT proname, pg_get_functiondef(oid) AS function_definition FROM pg_proc WHERE pronamespace = (SELECT oid FROM pg_namespace WHERE nspname = '" + schemaName + "')";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(functionsQuery)) {
            while (resultSet.next()) {
                String functionName = resultSet.getString("proname");
                String functionDefinition = resultSet.getString("function_definition");
                sqlBuilder.append("DROP FUNCTION IF EXISTS ").append(schemaName).append(".").append(functionName).append(";\n");
                sqlBuilder.append(functionDefinition).append(";\n\n");
            }
        }
    }

    private void exportTriggers(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String triggersQuery = "SELECT tgname, pg_get_triggerdef(oid) AS trigger_definition FROM pg_trigger";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(triggersQuery)) {
            while (resultSet.next()) {
                String triggerName = resultSet.getString("tgname");
                String triggerDefinition = resultSet.getString("trigger_definition");
                sqlBuilder.append("DROP TRIGGER IF EXISTS ").append(schemaName).append(".").append(triggerName).append(";\n");
                sqlBuilder.append(triggerDefinition).append(";\n\n");
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
