package ai.chat2db.plugin.clickhouse;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Objects;

public class ClickHouseDBManage extends DefaultDBManage implements DBManage {
    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTablesOrViewsOrDictionaries(connection, sqlBuilder, databaseName, schemaName,containData);
        exportFunctions(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportFunctions(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        String sql ="SELECT name,create_query from system.functions where origin='SQLUserDefined'";
        try(ResultSet resultSet=connection.createStatement().executeQuery(sql)){
            while (resultSet.next()) {
                sqlBuilder.append("DROP FUNCTION IF EXISTS ").append(resultSet.getString("name")).append(";")
                        .append("\n")
                        .append(resultSet.getString("create_query")).append(";").append("\n");
            }
        }
    }

    private void exportTablesOrViewsOrDictionaries(Connection connection, StringBuilder sqlBuilder, String databaseName, String schemaName, boolean containData) throws SQLException {
        String sql =String.format("SELECT create_table_query, has_own_data,engine,name from system.`tables` WHERE `database`='%s'", databaseName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String ddl = resultSet.getString("create_table_query");
                boolean dataFlag = resultSet.getInt("has_own_data") == 1;
                String tableType = resultSet.getString("engine");
                String tableOrViewName = resultSet.getString("name");
                if (Objects.equals("View", tableType)) {
                    sqlBuilder.append("DROP VIEW IF EXISTS ").append(databaseName).append(".").append(tableOrViewName)
                            .append(";").append("\n").append(ddl).append(";").append("\n");
                } else if (Objects.equals("Dictionary", tableType)) {
                    sqlBuilder.append("DROP DICTIONARY IF EXISTS ").append(databaseName).append(".").append(tableOrViewName)
                            .append(";").append("\n").append(ddl).append(";").append("\n");
                } else {
                    sqlBuilder.append("DROP TABLE IF EXISTS ").append(databaseName).append(".").append(tableOrViewName)
                            .append(";").append("\n").append(ddl).append(";").append("\n");
                    if (containData && dataFlag) {
                        exportTableData(connection,schemaName, tableOrViewName, sqlBuilder);
                    }
                }
            }
        }
    }


    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        String url = setDatabaseInJdbcUrl(connectInfo);
        connectInfo.setUrl(url);

        return super.getConnection(connectInfo);
    }

    private String setDatabaseInJdbcUrl(ConnectInfo connectInfo) {
        String databaseName;
        String url = connectInfo.getUrl();
        if (StringUtils.isBlank((databaseName = connectInfo.getDatabaseName())) && StringUtils.isBlank((databaseName = connectInfo.getSchemaName()))) {
            return url;
        }

        String connectAddress = connectInfo.getHost() + ":" + connectInfo.getPort();
        String[] addressSplit = url.split(connectAddress);
        String connectParams = addressSplit[1];
        if (connectParams.startsWith("/")) {
            // Remove / from connection parameters
            connectParams = connectParams.substring(1);
        }
        // Add database name
        return addressSplit[0] + connectAddress + "/" + databaseName + connectParams;
    }

    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE IF EXISTS " + databaseName + "." + tableName;
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }


}
