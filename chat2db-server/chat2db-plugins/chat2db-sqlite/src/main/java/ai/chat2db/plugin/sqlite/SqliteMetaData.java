package ai.chat2db.plugin.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.sql.SQLExecutor;
import com.google.common.collect.Lists;

public class SqliteMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
        return SQLExecutor.getInstance().executeSql(connection,sql, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("sql");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
    @Override
    public List<Database> databases(Connection connection) {
        return Lists.newArrayList(Database.builder().name("main").build());
    }

    @Override
    public List<Schema> schemas(Connection connection,String databaseName) {
        return Lists.newArrayList();
    }
}
