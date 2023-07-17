package ai.chat2db.plugin.postgresql;

import java.sql.Connection;
import java.sql.SQLException;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class PostgreSQLDBManage extends DefaultDBManage implements DBManage {
    @Override
    public void connectDatabase(Connection connection, String database) {
        try {
            SQLExecutor.getInstance().execute(connection,"SELECT pg_database_size('"+database+"');");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
