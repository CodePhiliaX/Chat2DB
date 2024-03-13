package ai.chat2db.plugin.hive;

import ai.chat2db.plugin.hive.builder.HiveSqlBuilder;
import ai.chat2db.plugin.hive.type.HiveColumnTypeEnum;
import ai.chat2db.plugin.hive.type.HiveIndexTypeEnum;
import ai.chat2db.spi.CommandExecutor;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableMeta;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HiveMetaData extends DefaultMetaService implements MetaData {

    @Override
    public List<Database> databases(Connection connection) {
        List<Database> databases = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection,"show databases", resultSet -> {
            try {
                while (resultSet.next()) {
                    String databaseName = resultSet.getString("database_name");
                    Database database = new Database();
                    database.setName(databaseName);
                    databases.add(database);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return databases;
        });
    }

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = new ArrayList<>();
        schemas.add(Schema.builder().databaseName(databaseName).name(databaseName).build());
        return schemas;
    }

    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
                           @NotEmpty String tableName) {
        String sql = "SHOW CREATE TABLE " + format(databaseName) + "."
                + format(tableName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                // 拼接建表语句
                sb.append(resultSet.getString("createtab_stmt"));
                sb.append("\r\n");
            }
            if (sb.length() > 0) {
                sb = sb.delete(sb.length() - 2, sb.length());
                sb.append(";");
                return sb.toString();
            }
            return null;
        });
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "`" + name + "`").collect(Collectors.joining("."));
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new HiveCommandExecutor();
    }

    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(HiveColumnTypeEnum.getTypes())
                //.charsets(HiveCharsetEnum.getCharsets())
                //.collations(HiveCollationEnum.getCollations())
                .indexTypes(HiveIndexTypeEnum.getIndexTypes())
                //.defaultValues(HiveDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new HiveSqlBuilder();
    }


    private static String SELECT_TABLE_SQL = "DESCRIBE EXTENDED %s";
    // TODO 待完善
    @Override
    public List<Table> tables(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(SELECT_TABLE_SQL, schemaName);
        if (StringUtils.isNotBlank(tableName)) {
            sql = sql + " and A.TABLE_NAME = '" + tableName + "'";
        }
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            List<Table> tables = new ArrayList<>();
            while (resultSet.next()) {
                Table table = new Table();
                table.setDatabaseName(databaseName);
                table.setSchemaName(schemaName);
                table.setName(resultSet.getString("TABLE_NAME"));
                table.setComment(resultSet.getString("COMMENTS"));
                tables.add(table);
            }
            return tables;
        });
    }

    public static String format(String name) {
        return "`" + name + "`";
    }
}

