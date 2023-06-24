/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.spi;

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
     *
     * @return
     */
    List<Database> databases();

    /**
     * Querying all schemas under a database
     *
     * @param databaseName
     * @return
     */
    List<Schema> schemas(String databaseName);

    /**
     * Querying DDL information
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

    /**
     * Querying all table under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Table> tables(@NotEmpty String databaseName, String schemaName, String tableName);

    /**
     * Querying all views under a schema.
     *
     * @param databaseName
     * @return
     */
    List<? extends Table> views(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all functions under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Function> functions(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all triggers under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Trigger> triggers(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all procedures under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Procedure> procedures(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all columns under a table.
     *
     * @param databaseName
     * @return
     */
    List<TableColumn> columns(@NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName);

    /**
     * Querying all columns under a table.
     *
     * @param databaseName
     * @return
     */
    List<TableColumn> columns(@NotEmpty String databaseName, String schemaName, String tableName,
        String columnName);

    /**
     * Querying all indexes under a table.
     *
     * @param databaseName
     * @return
     */
    List<TableIndex> indexes(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

}