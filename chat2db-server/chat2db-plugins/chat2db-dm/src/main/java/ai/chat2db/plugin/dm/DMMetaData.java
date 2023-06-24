package ai.chat2db.plugin.dm;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SqlUtils;

import java.sql.SQLException;

public class DMMetaData extends DefaultMetaService implements MetaData {
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        String selectObjectDDLSQL = String.format(
                "select dbms_metadata.get_ddl(%s, %s, %s) AS \"sql\" from dual",
                SqlUtils.formatSQLString("TABLE"), SqlUtils.formatSQLString(tableName),
                SqlUtils.formatSQLString(schemaName));
        return SQLExecutor.getInstance().executeSql(selectObjectDDLSQL, resultSet -> {
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
