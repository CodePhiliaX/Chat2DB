package ai.chat2db.spi;

import java.sql.Connection;
import java.util.List;

import ai.chat2db.spi.model.*;
import jakarta.validation.constraints.NotEmpty;

/**
 * Get database metadata information.
 *
 * @author jipengfei
 * @version : MetaData.java
 */
public interface MetaData {
    /**
     * Query all databases.
     * @param connection
     *
     * @return
     */
    List<Database> databases(Connection connection);

    /**
     * Querying all schemas under a database
     *
     * @param connection
     * @param databaseName
     * @return
     */
    List<Schema> schemas(Connection connection, String databaseName);

    /**
     * Querying DDL information
     *
     * @param connection
     * @param databaseName
     * @param tableName
     * @return
     */
    String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName);

    /**
     * Querying all table under a schema.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @return
     */
    List<Table> tables(Connection connection, @NotEmpty String databaseName, String schemaName, String tableName);

    /**
     * Querying all views under a schema.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Table> views(Connection connection, @NotEmpty String databaseName, String schemaName);

    /**
     * Querying all functions under a schema.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Function> functions(Connection connection, @NotEmpty String databaseName, String schemaName);

    /**
     * Querying all triggers under a schema.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Trigger> triggers(Connection connection, @NotEmpty String databaseName, String schemaName);

    /**
     * Querying all procedures under a schema.
     *
     * @param connection
     * @param schemaName
     * @param databaseName
     * @return
     */
    List<Procedure> procedures(Connection connection, @NotEmpty String databaseName, String schemaName);

    /**
     * Querying all columns under a table.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @return
     */
    List<TableColumn> columns(Connection connection, @NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName);

    /**
     * Querying all columns under a table.
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param columnName
     * @return
     */
    List<TableColumn> columns(Connection connection, @NotEmpty String databaseName, String schemaName, String tableName,
        String columnName);

    /**
     * Querying all indexes under a table.
     *
     * @param connection
     * @param databaseName
     * @param databaseName
     * @return
     */
    List<TableIndex> indexes(Connection connection, @NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName);

}