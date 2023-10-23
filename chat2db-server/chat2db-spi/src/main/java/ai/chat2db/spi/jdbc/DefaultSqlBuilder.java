package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;

public class DefaultSqlBuilder implements SqlBuilder {


    @Override
    public String buildCreateTableSql(Table table) {
        return null;
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        return null;
    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        return null;
    }

    public static String CREATE_DATABASE_SQL = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s";

    @Override
    public String buildCreateDatabaseSql(Database database) {
        return null;
    }

    @Override
    public String buildModifyDatabaseSql(Database oldDatabase, Database newDatabase) {
        return null;
    }

    @Override
    public String buildCreateSchemaSql(Schema schema) {
        return null;
    }

    @Override
    public String buildModifySchemaSql(String oldSchemaName, String newSchemaName) {
        return null;
    }
}
