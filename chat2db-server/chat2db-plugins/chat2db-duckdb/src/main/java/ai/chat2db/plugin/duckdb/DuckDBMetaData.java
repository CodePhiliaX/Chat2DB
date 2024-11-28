package ai.chat2db.plugin.duckdb;

import ai.chat2db.plugin.duckdb.builder.DuckDBSqlBuilder;
import ai.chat2db.plugin.duckdb.type.DuckDBColumnTypeEnum;
import ai.chat2db.plugin.duckdb.type.DuckDBDefaultValueEnum;
import ai.chat2db.plugin.duckdb.type.DuckDBIndexTypeEnum;
import ai.chat2db.spi.CommandExecutor;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static ai.chat2db.spi.util.SortUtils.sortDatabase;

public class DuckDBMetaData extends DefaultMetaService implements MetaData {

    private List<String> systemDatabases = Arrays.asList("information_schema", "temp", "main", "system");

    @Override
    public List<Database> databases(Connection connection) {
        List<Database> databases = SQLExecutor.getInstance().databases(connection);
        return sortDatabase(databases, systemDatabases, connection);
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new DuckDBCommandExecutor();
    }


    private static String TABLES_SQL
            = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG = '%s' AND TABLE_SCHEMA = '%s'";
    @Override
    public List<Table> tables(Connection connection, @NotEmpty String databaseName, String schemaName, String tableName) {
        String sql = String.format(TABLES_SQL, databaseName, schemaName);
        if(StringUtils.isNotBlank(tableName)){
            sql += " AND TABLE_NAME = '" + tableName + "'";
        }
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            List<Table> tables = new ArrayList<>();
            while (resultSet.next()) {
                Table table = new Table();
                table.setDatabaseName(databaseName);
                table.setSchemaName(schemaName);
                table.setName(resultSet.getString("table_name"));
                //table.setEngine(resultSet.getString("ENGINE"));
                //table.setRows(resultSet.getLong("TABLE_ROWS"));
                //table.setDataLength(resultSet.getLong("DATA_LENGTH"));
                //table.setCreateTime(resultSet.getString("CREATE_TIME"));
                //table.setUpdateTime(resultSet.getString("UPDATE_TIME"));
                //table.setCollate(resultSet.getString("TABLE_COLLATION"));
                table.setComment(resultSet.getString("TABLE_COMMENT"));
                tables.add(table);
            }
            return tables;
        });
    }


    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
                           @NotEmpty String tableName) {
        String sql = "SELECT sql FROM duckdb_tables() WHERE database_name = " + format(databaseName)
                + " AND schema_name = " + format(schemaName) + " AND table_name = " + format(tableName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("sql");
            }
            return null;
        });
    }

    public static String format(String tableName) {
        return "'" + tableName + "'";
    }

    private static String SELECT_TABLE_COLUMNS = "SELECT * FROM information_schema.COLUMNS  WHERE TABLE_SCHEMA =  '%s'  AND TABLE_NAME =  '%s'  order by ORDINAL_POSITION";

    @Override
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(SELECT_TABLE_COLUMNS, schemaName, tableName);
        List<TableColumn> tableColumns = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                TableColumn column = new TableColumn();
                column.setDatabaseName(databaseName);
                column.setTableName(tableName);
                column.setOldName(resultSet.getString("column_name"));
                column.setName(resultSet.getString("column_name"));
                //column.setColumnType(resultSet.getString("COLUMN_TYPE"));
                column.setColumnType(resultSet.getString("data_type").toUpperCase());
                //column.setDataType(resultSet.getInt("DATA_TYPE"));
                column.setDefaultValue(resultSet.getString("column_default"));
                //column.setAutoIncrement(resultSet.getString("EXTRA").contains("auto_increment"));
                column.setComment(resultSet.getString("COLUMN_COMMENT"));
                //column.setPrimaryKey("PRI".equalsIgnoreCase(resultSet.getString("COLUMN_KEY")));
                column.setNullable("YES".equalsIgnoreCase(resultSet.getString("is_nullable")) ? 1 : 0);
                column.setOrdinalPosition(resultSet.getInt("ordinal_position"));
                column.setDecimalDigits(resultSet.getInt("numeric_precision"));
                column.setCharSetName(resultSet.getString("character_set_name"));
                column.setCollationName(resultSet.getString("collation_name"));
                setColumnSize(column, resultSet.getString("data_type"));
                tableColumns.add(column);
            }
            return tableColumns;
        });
    }

    private void setColumnSize(TableColumn column, String columnType) {
        try {
            if (columnType.contains("(")) {
                String size = columnType.substring(columnType.indexOf("(") + 1, columnType.indexOf(")"));
                if ("SET".equalsIgnoreCase(column.getColumnType()) || "ENUM".equalsIgnoreCase(column.getColumnType())) {
                    column.setValue(size);
                } else {
                    if (size.contains(",")) {
                        String[] sizes = size.split(",");
                        if (StringUtils.isNotBlank(sizes[0])) {
                            column.setColumnSize(Integer.parseInt(sizes[0]));
                        }
                        if (StringUtils.isNotBlank(sizes[1])) {
                            column.setDecimalDigits(Integer.parseInt(sizes[1]));
                        }
                    } else {
                        column.setColumnSize(Integer.parseInt(size));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private static String VIEW_DDL_SQL = "SELECT sql FROM duckdb_views() WHERE database_name = '%s' AND schema_name = '%s' AND view_name = '%s'";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_DDL_SQL, databaseName, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("sql"));
            }
            return table;
        });
    }


    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        StringBuilder queryBuf = new StringBuilder("SELECT * FROM duckdb_indexes WHERE schema_name = ");
        queryBuf.append("'").append(schemaName).append("'");
        queryBuf.append(" and table_name = ");
        queryBuf.append("'").append(tableName).append("'");
        return SQLExecutor.getInstance().execute(connection, queryBuf.toString(), resultSet -> {
            LinkedHashMap<String, TableIndex> map = new LinkedHashMap();
            while (resultSet.next()) {
                String keyName = resultSet.getString("Key_name");
                TableIndex tableIndex = map.get(keyName);
                if (tableIndex != null) {
                    List<TableIndexColumn> columnList = tableIndex.getColumnList();
                    columnList.add(getTableIndexColumn(resultSet));
                    columnList = columnList.stream().sorted(Comparator.comparing(TableIndexColumn::getOrdinalPosition))
                            .collect(Collectors.toList());
                    tableIndex.setColumnList(columnList);
                } else {
                    TableIndex index = new TableIndex();
                    index.setDatabaseName(databaseName);
                    index.setSchemaName(schemaName);
                    index.setTableName(tableName);
                    index.setName(keyName);
                    index.setUnique(!resultSet.getBoolean("Non_unique"));
                    index.setType(resultSet.getString("Index_type"));
                    index.setComment(resultSet.getString("Index_comment"));
                    List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
                    tableIndexColumns.add(getTableIndexColumn(resultSet));
                    index.setColumnList(tableIndexColumns);
                    if ("PRIMARY".equalsIgnoreCase(keyName)) {
                        index.setType(DuckDBIndexTypeEnum.PRIMARY_KEY.getName());
                    } else if (index.getUnique()) {
                        index.setType(DuckDBIndexTypeEnum.UNIQUE.getName());
                    } else {
                        index.setType(DuckDBIndexTypeEnum.NORMAL.getName());
                    }
                    map.put(keyName, index);
                }
            }
            return map.values().stream().collect(Collectors.toList());
        });

    }

    private TableIndexColumn getTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("Column_name"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("Seq_in_index"));
        tableIndexColumn.setCollation(resultSet.getString("Collation"));
        tableIndexColumn.setCardinality(resultSet.getLong("Cardinality"));
        tableIndexColumn.setSubPart(resultSet.getLong("Sub_part"));
        String collation = resultSet.getString("Collation");
        if ("a".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("ASC");
        } else if ("d".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("DESC");
        }
        return tableIndexColumn;
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new DuckDBSqlBuilder();
    }

    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(DuckDBColumnTypeEnum.getTypes())
                //.collations(MysqlCollationEnum.getCollations())
                .indexTypes(DuckDBIndexTypeEnum.getIndexTypes())
                .defaultValues(DuckDBDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).collect(Collectors.joining("."));
    }

//    @Override
//    public ValueHandler getValueHandler() {
//        return new MysqlValueHandler();
//    }

    /*@Override
    public ValueProcessor getValueProcessor() {
        return new DuckDBValueProcessor();
    }*/

    @Override
    public List<String> getSystemDatabases() {
        return systemDatabases;
    }

}
