package ai.chat2db.spi.jdbc;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author jipengfei
 * @version : DefaultMetaService.java
 */
public class DefaultMetaService implements MetaData {
    @Override
    public List<Database> databases(Connection connection) {
        List<String> dataBases = SQLExecutor.getInstance().databases(connection);
        return dataBases.stream().map(str -> Database.builder().name(str).build()).collect(Collectors.toList());

    }

    @Override
    public List<Schema> schemas(Connection connection,String databaseName) {
        List<Map<String, String>> maps = SQLExecutor.getInstance().schemas(connection, databaseName, null);
        return maps.stream().map(map -> map2Schema(map)).collect(Collectors.toList());

    }

    private Schema map2Schema(Map<String, String> map) {
        Schema schema = new Schema();
        try {
            BeanUtils.populate(schema, map);
        } catch (Exception e) {
        }
        return schema;
    }

    @Override
    public String tableDDL(Connection connection,String databaseName, String schemaName, String tableName) {
        return null;
    }

    @Override
    public List<Table> tables(Connection connection,String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().tables(connection,databaseName, schemaName, tableName, new String[]{"TABLE"});
    }

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        return null;
    }

    @Override
    public List<Table> views(Connection connection,String databaseName, String schemaName) {
        return SQLExecutor.getInstance().tables(connection,databaseName, schemaName, null, new String[]{"VIEW"});
    }

    @Override
    public List<Function> functions(Connection connection,String databaseName, String schemaName) {
        return SQLExecutor.getInstance().functions(connection,databaseName, schemaName);
    }

    @Override
    public List<Trigger> triggers(Connection connection,String databaseName, String schemaName) {
        return null;
    }

    @Override
    public List<Procedure> procedures(Connection connection,String databaseName, String schemaName) {
        return SQLExecutor.getInstance().procedures(connection,databaseName, schemaName);
    }

    @Override
    public List<TableColumn> columns(Connection connection,String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().columns(connection,databaseName, schemaName, tableName, null);
    }

    @Override
    public List<TableColumn> columns(Connection connection,String databaseName, String schemaName, String tableName,
        String columnName) {
        return SQLExecutor.getInstance().columns(connection,databaseName, schemaName, tableName, columnName);
    }

    @Override
    public List<TableIndex> indexes(Connection connection,String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().indexes(connection,databaseName, schemaName, tableName);
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
}