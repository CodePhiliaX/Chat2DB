package ai.chat2db.plugin.dm;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class DMDBManage implements DBManage {
    @Override
    public void connectDatabase(String database) {

    }

    @Override
    public void modifyDatabase(String databaseName, String newDatabaseName) {

    }

    @Override
    public void createDatabase(String databaseName) {

    }

    @Override
    public void dropDatabase(String databaseName) {

    }

    @Override
    public void createSchema(String databaseName, String schemaName) {

    }

    @Override
    public void dropSchema(String databaseName, String schemaName) {

    }

    @Override
    public void modifySchema(String databaseName, String schemaName, String newSchemaName) {

    }

    @Override
    public void dropTable(String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE IF EXISTS " +tableName;
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }
}
