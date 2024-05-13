package ai.chat2db.plugin.kingbase;

import java.sql.Connection;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.StringUtils;

public class KingBaseDBManage extends DefaultDBManage implements DBManage {

    @Override
    public void connectDatabase(Connection connection, String database) {
        try {
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
            if (!StringUtils.isEmpty(connectInfo.getSchemaName())) {
                SQLExecutor.getInstance().execute(connection, "SET search_path TO \"" + connectInfo.getSchemaName() + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        String sql = "drop table if exists " +tableName;
        SQLExecutor.getInstance().execute(connection,sql, resultSet -> null);
    }
}
