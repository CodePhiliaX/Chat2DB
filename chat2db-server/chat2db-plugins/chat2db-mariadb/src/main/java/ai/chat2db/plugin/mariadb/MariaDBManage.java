package ai.chat2db.plugin.mariadb;

import java.sql.Connection;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class MariaDBManage extends DefaultDBManage implements DBManage {


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " +tableName ;
        SQLExecutor.getInstance().executeSql(connection,sql, resultSet -> null);
    }
}
