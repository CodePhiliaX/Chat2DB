package ai.chat2db.plugin.snowflake;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SortUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class SnowFlakeMetaData extends DefaultMetaService implements MetaData {


    private List<String> systemSchemas = Arrays.asList("INFORMATION_SCHEMA", "PUBLIC", "SCHEMA");

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = SQLExecutor.getInstance().schemas(connection, databaseName, null);
        return SortUtils.sortSchema(schemas, systemSchemas);
    }

   /* @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "`" + name + "`").collect(Collectors.joining("."));
    }*/
}
