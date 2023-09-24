package ai.chat2db.spi;

import ai.chat2db.spi.model.Table;

public interface SqlBuilder {

    /**
     * Generate create table sql
     *
     * @param table
     * @return
     */
    String buildCreateTableSql(Table table);


    /**
     * Generate modify table sql
     *
     * @param newTable
     * @param oldTable
     * @return
     */
    String buildModifyTaleSql(Table oldTable, Table newTable);
}
