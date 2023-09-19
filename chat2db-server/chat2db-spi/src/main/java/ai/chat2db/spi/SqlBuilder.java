package ai.chat2db.spi;

import ai.chat2db.spi.model.Table;

public interface SqlBuilder {

    /**
     * Generate create table sql
     *
     * @param databaseName
     * @param schemaName
     * @param table
     * @return
     */
    String generateCreateTableSql(String databaseName, String schemaName, Table table);


    /**
     * Generate modify table sql
     *
     * @param databaseName
     * @param schemaName
     * @param newTable
     * @param oldTable
     * @return
     */
    String generateModifyTaleSql(String databaseName, String schemaName, Table newTable, Table oldTable);
}
