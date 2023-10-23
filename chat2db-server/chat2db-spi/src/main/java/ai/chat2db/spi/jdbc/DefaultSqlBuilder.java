package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.SqlBuilder;
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
}
