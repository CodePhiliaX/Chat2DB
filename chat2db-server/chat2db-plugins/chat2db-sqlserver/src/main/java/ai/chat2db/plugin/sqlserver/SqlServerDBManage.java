package ai.chat2db.plugin.sqlserver;

import java.sql.Connection;
import java.sql.SQLException;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

public class SqlServerDBManage extends DefaultDBManage implements DBManage {
    @Override
    public void connectDatabase(Connection connection, String database) {
        try {
            SQLExecutor.getInstance().execute(connection, "use [" + database + "];");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
