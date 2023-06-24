package ai.chat2db.plugin.redis;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RedisMetaData extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
        return "";
    }


    @Override
    public List<Database> databases() {
        List<Database> databases = new ArrayList<>();
        return SQLExecutor.getInstance().executeSql("config get databases", resultSet -> {
            try {
                if (resultSet.next()) {
                    Object count = resultSet.getObject(2);
                    if(StringUtils.isNotBlank(count.toString())) {
                        for (int i = 0; i < Integer.parseInt(count.toString()); i++) {
                            Database database = Database.builder().name(String.valueOf(i)).build();
                            databases.add(database);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return databases;
        });
    }

    @Override
    public List<Table> tables(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().executeSql("scan 0 MATCH * COUNT 1000", resultSet -> {
            List<Table> tables = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    ArrayList list = (ArrayList)resultSet.getObject(2);
                    for (Object object : list) {
                        Table table = new Table();
                        table.setName(object.toString());
                        tables.add(table);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return tables;
        });
    }
}
