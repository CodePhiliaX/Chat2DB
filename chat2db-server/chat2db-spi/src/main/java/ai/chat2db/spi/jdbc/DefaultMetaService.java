package ai.chat2db.spi.jdbc;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;

/**
 * @author jipengfei
 * @version : DefaultMetaService.java
 */
public class DefaultMetaService implements MetaData {
    @Override
    public List<Database> databases(Connection connection) {
        return SQLExecutor.getInstance().databases(connection);
    }

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        return SQLExecutor.getInstance().schemas(connection, databaseName, null);
    }

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        return null;
    }

    @Override
    public List<Table> tables(Connection connection, String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().tables(connection, databaseName, schemaName, tableName, new String[]{"TABLE"});
    }

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        return null;
    }

    @Override
    public List<Table> views(Connection connection, String databaseName, String schemaName) {
        return SQLExecutor.getInstance().tables(connection, databaseName, schemaName, null, new String[]{"VIEW"});
    }

    @Override
    public List<Function> functions(Connection connection, String databaseName, String schemaName) {
        return SQLExecutor.getInstance().functions(connection, databaseName, schemaName);
    }

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        return null;
    }

    @Override
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        return SQLExecutor.getInstance().procedures(connection, databaseName, schemaName);
    }

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().columns(connection, databaseName, schemaName, tableName, null);
    }

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName,
                                     String columnName) {
        return SQLExecutor.getInstance().columns(connection, databaseName, schemaName, tableName, columnName);
    }

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().indexes(connection, databaseName, schemaName, tableName);
    }

    @Override
    public Function function(Connection connection, String databaseName, String schemaName, String functionName) {
        return null;
    }

    @Override
    public Trigger trigger(Connection connection, String databaseName, String schemaName, String triggerName) {
        return null;
    }

    @Override
    public Procedure procedure(Connection connection, String databaseName, String schemaName, String procedureName) {
        return null;
    }

    @Override
    public List<Type> types(Connection connection) {
        return SQLExecutor.getInstance().types(connection);
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new DefaultSqlBuilder();
    }
}