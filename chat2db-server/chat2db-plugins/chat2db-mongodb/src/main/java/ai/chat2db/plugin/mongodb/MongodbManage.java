package ai.chat2db.plugin.mongodb;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class MongodbManage extends DefaultDBManage implements DBManage {
    @Override
    public void connectDatabase(Connection connection, String database) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (Objects.isNull(connectInfo) || !StringUtils.hasText(connectInfo.getSchemaName())) {
            return;
        }
        String schemaName = connectInfo.getSchemaName();
        if (!StringUtils.hasText(schemaName)) {
            return;
        }
        try {
            SQLExecutor.getInstance().execute(connection, "use " + schemaName + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = " db. " + tableName + ".drop();";
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

}
