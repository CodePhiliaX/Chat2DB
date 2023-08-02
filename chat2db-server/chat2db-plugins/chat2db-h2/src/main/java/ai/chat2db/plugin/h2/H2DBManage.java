package ai.chat2db.plugin.h2;

import java.sql.Connection;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class H2DBManage extends DefaultDBManage implements DBManage {


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " +tableName;
        SQLExecutor.getInstance().executeSql(connection,sql, resultSet -> null);
    }
}
