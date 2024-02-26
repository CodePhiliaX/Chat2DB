package ai.chat2db.spi;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.ConnectInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.sql.Connection;

/**
 * @author jipengfei
 * @version : DBManage.java
 */
public interface DBManage {

    /**
     * Create connection
     *
     * @param connectInfo
     */
    Connection getConnection(ConnectInfo connectInfo);

    /**
     * @param database
     */
    void connectDatabase(Connection connection, String database);

    /**
     * 修改数据库名称
     *
     * @param databaseName
     * @param newDatabaseName
     */
    void modifyDatabase(Connection connection, String databaseName, String newDatabaseName);

    /**
     * 创建数据库
     *
     * @param databaseName
     */
    void createDatabase(Connection connection, String databaseName);

    /**
     * 删除数据库
     *
     * @param databaseName
     */
    void dropDatabase(Connection connection, String databaseName);

    /**
     * 创建schema
     *
     * @param databaseName
     * @param schemaName
     */
    void createSchema(Connection connection, String databaseName, String schemaName);

    /**
     * 删除schema
     *
     * @param databaseName
     * @param schemaName
     */
    void dropSchema(Connection connection, String databaseName, String schemaName);

    /**
     * 修改schema
     *
     * @param databaseName
     * @param schemaName
     * @param newSchemaName
     */
    void modifySchema(Connection connection, String databaseName, String schemaName, String newSchemaName);

    /**
     * 删除表结构
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    void dropTable(Connection connection, @NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

    /**
     * 删除函数
     *
     * @param databaseName
     * @param functionName
     * @return
     */
    void dropFunction(Connection connection, @NotEmpty String databaseName, String schemaName,
                      @NotEmpty String functionName);

    /**
     * 删除触发器
     *
     * @param databaseName
     * @param triggerName
     * @return
     */
    void dropTrigger(Connection connection, @NotEmpty String databaseName, String schemaName,
                     @NotEmpty String triggerName);

    /**
     * 删除存储过程
     *
     * @param databaseName
     * @param triggerName
     * @return
     */
    void dropProcedure(Connection connection, @NotEmpty String databaseName, String schemaName,
                       @NotEmpty String triggerName);

    /**
     * 更新存储过程
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param procedure
     */
    void updateProcedure(Connection connection, @NotEmpty String databaseName, String schemaName, @NotNull Procedure procedure);
}