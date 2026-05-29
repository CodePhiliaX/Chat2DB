package ai.chat2db.spi;

import ai.chat2db.spi.model.*;

import java.util.List;

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
     *
     * @param database
     * @return
     */
    String buildCreateDatabaseSql(Database database);


    /**
     * @param oldDatabase
     * @param newDatabase
     * @return
     */
    String buildModifyDatabaseSql(Database oldDatabase, Database newDatabase);


    /**
     * @param schemaName
     * @return
     */
    String buildCreateSchemaSql(Schema schemaName);


    /**
     * @param oldSchemaName
     * @param newSchemaName
     * @return
     */
    String buildModifySchemaSql(String oldSchemaName, String newSchemaName);

    /**
     * @param originSql
     * @param orderByList
     * @return
     */
    String buildOrderBySql(String originSql, List<OrderBy> orderByList);


    /**
     * generate sql based on results
     */
    String generateSqlBasedOnResults(String tableName, List<Header> headerList, List<ResultOperation> operations);

    /**
     * 构建导入SQL（参数化形式，返回SQL模板供PreparedStatement使用）
     *
     * @param tableName         表名
     * @param headerList        表头元数据
     * @param primaryKeyColumns 主键列名
     * @param mode              导入模式
     * @return SQL模板字符串（使用?占位符）
     */
    String buildImportSql(String tableName, List<Header> headerList, List<String> primaryKeyColumns, String mode);

    /**
     * Generate add foreign key sql
     */
    default String buildAddForeignKeySql(ForeignKey fk) {
        return null;
    }

    /**
     * Generate drop foreign key sql
     */
    default String buildDropForeignKeySql(ForeignKey fk) {
        return null;
    }

    /**
     * Generate OPTIMIZE TABLE SQL
     * Returns null if not supported by this database
     */
    default String buildOptimizeTableSql(String databaseName, String schemaName, String tableName) {
        return null;
    }

    /**
     * Generate ANALYZE TABLE SQL
     * Returns null if not supported by this database
     */
    default String buildAnalyzeTableSql(String databaseName, String schemaName, String tableName) {
        return null;
    }

    /**
     * Generate EXPLAIN SQL for query execution plan
     * Returns null if not supported by this database
     *
     * @param originalSql the SQL statement to explain
     * @return EXPLAIN SQL statement
     */
    default String buildExplainSql(String originalSql) {
        return null;
    }

}
