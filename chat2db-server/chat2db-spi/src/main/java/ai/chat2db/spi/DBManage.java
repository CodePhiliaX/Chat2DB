package ai.chat2db.spi;

import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.ConnectInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

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
     * Modify database name
     *
     * @param databaseName
     * @param newDatabaseName
     */
    void modifyDatabase(Connection connection, String databaseName, String newDatabaseName);

    /**
     * Create database
     *
     * @param databaseName
     */
    void createDatabase(Connection connection, String databaseName);

    /**
     * Delete database
     *
     * @param databaseName
     */
    void dropDatabase(Connection connection, String databaseName);

    /**
     * Create schema
     *
     * @param databaseName
     * @param schemaName
     */
    void createSchema(Connection connection, String databaseName, String schemaName);

    /**
     * Delete schema
     *
     * @param databaseName
     * @param schemaName
     */
    void dropSchema(Connection connection, String databaseName, String schemaName);

    /**
     * Modify schema
     *
     * @param databaseName
     * @param schemaName
     * @param newSchemaName
     */
    void modifySchema(Connection connection, String databaseName, String schemaName, String newSchemaName);

    /**
     * Delete table structure
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    void dropTable(Connection connection, @NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

    /**
     * delete function
     *
     * @param databaseName
     * @param functionName
     * @return
     */
    void dropFunction(Connection connection, @NotEmpty String databaseName, String schemaName,
                      @NotEmpty String functionName);

    /**
     * delete trigger
     *
     * @param databaseName
     * @param triggerName
     * @return
     */
    void dropTrigger(Connection connection, @NotEmpty String databaseName, String schemaName,
                     @NotEmpty String triggerName);

    /**
     * Delete stored procedure
     *
     * @param databaseName
     * @param triggerName
     * @return
     */
    void dropProcedure(Connection connection, @NotEmpty String databaseName, String schemaName,
                       @NotEmpty String triggerName);

    /**
     * Update stored procedure
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param procedure
     */
    void updateProcedure(Connection connection, @NotEmpty String databaseName, String schemaName, @NotNull Procedure procedure) throws SQLException;

    String exportDatabase(Connection connection, String databaseName, String schemaName,boolean containData) throws SQLException;

    String exportDatabaseData(Connection connection, String databaseName, String schemaName,String tableName) throws SQLException;
}