
package ai.chat2db.spi;

import jakarta.validation.constraints.NotEmpty;

/**
 * @author jipengfei
 * @version : DBManage.java
 */
public interface DBManage {

    /**
     * @param database
     */
    void connectDatabase(String database);

    /**
     * 修改数据库名称
     * @param databaseName
     * @param newDatabaseName
     */
    void modifyDatabase(String databaseName, String newDatabaseName);


    /**
     * 创建数据库
     * @param databaseName
     */
    void createDatabase(String databaseName);


    /**
     * 删除数据库
     * @param databaseName
     */
    void dropDatabase(String databaseName);



    /**
     * 创建schema
     * @param databaseName
     * @param schemaName
     */
    void createSchema(String databaseName, String schemaName);

    /**
     * 删除schema
     * @param databaseName
     * @param schemaName
     */
    void dropSchema(String databaseName, String schemaName);

    /**
     * 修改schema
     * @param databaseName
     * @param schemaName
     * @param newSchemaName
     */
    void modifySchema(String databaseName, String schemaName, String newSchemaName);


    /**
     * 删除表结构
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    void dropTable(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);
}