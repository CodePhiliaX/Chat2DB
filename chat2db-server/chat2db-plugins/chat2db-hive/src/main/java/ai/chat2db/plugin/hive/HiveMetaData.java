package ai.chat2db.plugin.hive;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;

import java.sql.SQLException;

public class HiveMetaData extends DefaultMetaService implements MetaData {

    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
        String sql = """
                SHOW CREATE TABLE `%s`.`%s`
                """.formatted(databaseName,tableName);
        return SQLExecutor.getInstance().executeSql(sql, resultSet -> {
            try {
                StringBuilder ddl = new StringBuilder();
                while (resultSet.next()) {
                    ddl.append(resultSet.getString("createtab_stmt"));
                }
                if (!ddl.isEmpty()) {
                    return ddl.toString();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

}
