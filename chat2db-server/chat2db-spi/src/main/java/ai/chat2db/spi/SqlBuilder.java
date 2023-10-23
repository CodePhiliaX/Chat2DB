package ai.chat2db.spi;

import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
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


    /**
     * Generate page limit sql
     *
     * @param sql
     * @param offset
     * @param pageNo
     * @param pageSize
     * @return
     */
    String pageLimit(String sql, int offset, int pageNo, int pageSize);


    /**
     * Generate create database sql
     * @param database
     * @return
     */
    String buildCreateDatabaseSql(Database database);


    /**
     *
     * @param oldDatabase
     * @param newDatabase
     * @return
     */
    String buildModifyDatabaseSql(Database oldDatabase, Database newDatabase);


    /**
     *
     * @param schemaName
     * @return
     */
    String buildCreateSchemaSql(Schema schemaName);


    /**
     *
     * @param oldSchemaName
     * @param newSchemaName
     * @return
     */
    String buildModifySchemaSql(String oldSchemaName, String newSchemaName);
}
