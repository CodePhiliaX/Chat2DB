
package ai.chat2db.spi.jdbc;

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
    public List<Database> databases() {
        List<String> dataBases = SQLExecutor.getInstance().databases();
        return dataBases.stream().map(str -> Database.builder().name(str).build()).collect(Collectors.toList());

    }

    @Override
    public List<Schema> schemas(String databaseName) {
        List<Map<String, String>> maps = SQLExecutor.getInstance().schemas(databaseName, null);
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
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        return null;
    }

    @Override
    public List<Table> tables(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().tables(databaseName, schemaName, tableName, new String[]{"TABLE"});
    }

    @Override
    public List<? extends Table> views(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().tables(databaseName, schemaName, null, new String[]{"VIEW"});
    }

    @Override
    public List<Function> functions(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().functions(databaseName, schemaName);
    }

    @Override
    public List<Trigger> triggers(String databaseName, String schemaName) {
        return null;
    }

    @Override
    public List<Procedure> procedures(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().procedures(databaseName, schemaName);
    }

    @Override
    public List<TableColumn> columns(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().columns(databaseName, schemaName, tableName, null);
    }

    @Override
    public List<TableColumn> columns(String databaseName, String schemaName, String tableName,
                                     String columnName) {
        return SQLExecutor.getInstance().columns(databaseName, schemaName, tableName, columnName);
    }

    @Override
    public List<TableIndex> indexes(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().indexes(databaseName, schemaName, tableName);
    }
}