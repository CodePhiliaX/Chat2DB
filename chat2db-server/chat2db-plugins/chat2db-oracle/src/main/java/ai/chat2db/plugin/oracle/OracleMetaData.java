package ai.chat2db.plugin.oracle;

import java.sql.Connection;
import java.sql.SQLException;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.sql.SQLExecutor;

public class OracleMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "select dbms_metadata.get_ddl('TABLE','"+tableName+"') as sql from dual,"
                + "user_tables where table_name = '" + tableName + "'";
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
}
