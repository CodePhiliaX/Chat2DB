package ai.chat2db.plugin.clickhouse;

import java.sql.Connection;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class ClickHouseDBManage extends DefaultDBManage implements DBManage {


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE IF EXISTS " + databaseName + "." + tableName;
        SQLExecutor.getInstance().executeSql(connection, sql, resultSet -> null);
    }


}
