package ai.chat2db.spi.jdbc;

import java.sql.Connection;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.sql.SQLExecutor;

/**
 * @author jipengfei
 * @version : DefaultDBManage.java
 */
public class DefaultDBManage implements DBManage {

    @Override
    public void connectDatabase(Connection connection,String database) {

    }

    @Override
    public void modifyDatabase(Connection connection,String databaseName, String newDatabaseName) {

    }

    @Override
    public void createDatabase(Connection connection,String databaseName) {

    }

    @Override
    public void dropDatabase(Connection connection,String databaseName) {

    }

    @Override
    public void createSchema(Connection connection,String databaseName, String schemaName) {

    }

    @Override
    public void dropSchema(Connection connection,String databaseName, String schemaName) {

    }

    @Override
    public void modifySchema(Connection connection,String databaseName, String schemaName, String newSchemaName) {

    }

    @Override
    public void dropTable(Connection connection,String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE "+ tableName ;
        SQLExecutor.getInstance().executeSql(connection,sql, resultSet -> null);
    }
}