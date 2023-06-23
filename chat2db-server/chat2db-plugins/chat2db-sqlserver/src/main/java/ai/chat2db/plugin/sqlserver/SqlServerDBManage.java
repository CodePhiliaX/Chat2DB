package ai.chat2db.plugin.sqlserver;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.sql.SQLExecutor;

import java.sql.SQLException;

public class SqlServerDBManage implements DBManage {
    @Override
    public void connectDatabase(String database) {
        try {
            SQLExecutor.getInstance().execute("use " + database + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    }
}
