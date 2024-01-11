package ai.chat2db.plugin.oceanbase;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OceanBaseMetaData extends DefaultMetaService implements MetaData {


    @Override
    public List<Table> tables(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "SHOW TABLE STATUS FROM "+ databaseName + " where name = '" + tableName + "';";
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            List<Table> tables = new ArrayList<>();
            while (resultSet.next()) {
                Table table = new Table();
                table.setDatabaseName(databaseName);
                table.setSchemaName(schemaName);
                table.setName(resultSet.getString("NAME"));
                table.setComment(resultSet.getString("COMMENT"));
                tables.add(table);
            }
            return tables;
        });
    }

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName) {
        return null;
        /*String sql = String.format(SELECT_TABLE_COLUMNS, databaseName, tableName);
        List<TableColumn> tableColumns = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                TableColumn column = new TableColumn();
                column.setDatabaseName(databaseName);
                column.setTableName(tableName);
                column.setOldName(resultSet.getString("COLUMN_NAME"));
                column.setName(resultSet.getString("COLUMN_NAME"));
                //column.setColumnType(resultSet.getString("COLUMN_TYPE"));
                column.setColumnType(resultSet.getString("DATA_TYPE").toUpperCase());
                //column.setDataType(resultSet.getInt("DATA_TYPE"));
                column.setDefaultValue(resultSet.getString("COLUMN_DEFAULT"));
                column.setAutoIncrement(resultSet.getString("EXTRA").contains("auto_increment"));
                column.setComment(resultSet.getString("COLUMN_COMMENT"));
                column.setPrimaryKey("PRI".equalsIgnoreCase(resultSet.getString("COLUMN_KEY")));
                column.setNullable("YES".equalsIgnoreCase(resultSet.getString("IS_NULLABLE")) ? 1 : 0);
                column.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
                column.setDecimalDigits(resultSet.getInt("NUMERIC_SCALE"));
                column.setCharSetName(resultSet.getString("CHARACTER_SET_NAME"));
                column.setCollationName(resultSet.getString("COLLATION_NAME"));
                setColumnSize(column, resultSet.getString("COLUMN_TYPE"));
                tableColumns.add(column);
            }
            return tableColumns;
        });*/
    }
    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "`" + name + "`").collect(Collectors.joining("."));
    }
}
