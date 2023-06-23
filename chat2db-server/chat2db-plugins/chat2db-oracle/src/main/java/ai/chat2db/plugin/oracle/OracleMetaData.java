package ai.chat2db.plugin.oracle;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.sql.SQLExecutor;

import java.sql.SQLException;

public class OracleMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        String sql = "select dbms_metadata.get_ddl('TABLE','"+tableName+"') as sql from dual,"
                + "user_tables where table_name = '" + tableName + "'";
        return SQLExecutor.getInstance().executeSql(sql, resultSet -> {
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
