package ai.chat2db.plugin.snowflake;

import ai.chat2db.plugin.snowflake.builder.SnowflakeSqlBuilder;
import ai.chat2db.plugin.snowflake.type.SnowflakeCharsetEnum;
import ai.chat2db.plugin.snowflake.type.SnowflakeCollationEnum;
import ai.chat2db.plugin.snowflake.type.SnowflakeColumnTypeEnum;
import ai.chat2db.plugin.snowflake.type.SnowflakeDefaultValueEnum;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableMeta;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SortUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class SnowflakeMetaData extends DefaultMetaService implements MetaData {


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

    private static String VIEW_SQL
            = "SELECT TABLE_SCHEMA AS DatabaseName, TABLE_NAME AS ViewName, VIEW_DEFINITION AS DEFINITION, CHECK_OPTION, "
            + "IS_UPDATABLE FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_CATALOG = '%s' AND TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";


    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, databaseName, schemaName , viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("DEFINITION").substring(resultSet.getString("DEFINITION").indexOf("as")+3));
            }
            return table;
        });
    }


    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(SnowflakeColumnTypeEnum.getTypes())
                .charsets(SnowflakeCharsetEnum.getCharsets())
                .collations(SnowflakeCollationEnum.getCollations())
                .defaultValues(SnowflakeDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new SnowflakeSqlBuilder();
    }

}
