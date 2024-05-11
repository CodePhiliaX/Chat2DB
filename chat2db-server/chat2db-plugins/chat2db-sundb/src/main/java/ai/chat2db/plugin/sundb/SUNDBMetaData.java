package ai.chat2db.plugin.sundb;

import ai.chat2db.plugin.sundb.builder.SUNDBSqlBuilder;
import ai.chat2db.plugin.sundb.type.SUNDBColumnTypeEnum;
import ai.chat2db.plugin.sundb.type.SUNDBDefaultValueEnum;
import ai.chat2db.plugin.sundb.type.SUNDBIndexTypeEnum;
import ai.chat2db.plugin.sundb.type.SUNDBObjectTypeEnum;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.SortUtils;
import com.google.common.collect.Lists;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SUNDBMetaData extends DefaultMetaService implements MetaData {

    private List<String> systemSchemas = Arrays.asList("DEFINITION_SCHEMA", "DICTIONARY_SCHEMA", "FIXED_TABLE_SCHEMA", "INFORMATION_SCHEMA",
            "PERFORMANCE_VIEW_SCHEMA", "PUBLIC");

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = SQLExecutor.getInstance().schemas(connection, databaseName, null);
        return SortUtils.sortSchema(schemas, systemSchemas);
    }

    private String format(String tableName) {
        return "\"" + tableName + "\"";
    }

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "SELECT * FROM \"%s\".\"%s\" LIMIT 1";
        StringBuilder ddlBuilder = new StringBuilder();
        String tableDDLSql = String.format(sql, schemaName, tableName);
        String ddl = null;
        ResultSet resultSet = null;

        try {
            Integer Scale;
            resultSet = connection.createStatement().executeQuery(tableDDLSql);
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int colCount = rsMetaData.getColumnCount();
            ddlBuilder.append("CREATE TABLE ").append("\"").append(schemaName).append("\"").append(".")
                    .append("\"").append(tableName).append("\" \n( ");
            int i = 1;
            while (true) {
                if (i >= colCount) {
                    ddlBuilder.append(rsMetaData.getColumnName(colCount) + " ");
                    ddlBuilder.append(rsMetaData.getColumnTypeName(colCount));
                    Integer Precision = 0;
                    Scale = 0;
                    Precision = rsMetaData.getPrecision(colCount);
                    Scale = rsMetaData.getScale(colCount);
                    if (Precision != null && Precision != 0) {
                        ddlBuilder.append("(").append(Precision);
                        if (rsMetaData.getColumnTypeName(colCount) == "NUMBER") {
                            ddlBuilder.append(",").append(Scale).append(")");
                        } else {
                            ddlBuilder.append(" )");
                        }
                    }

                    String constraintSql = "SELECT\r\nTC.TABLE_SCHEMA,\r\nTC.TABLE_NAME,\r\nTC.CONSTRAINT_NAME,\r\nTC.CONSTRAINT_TYPE,\r\nUCC.COLUMN_NAME\r\nFROM TABLE_CONSTRAINTS TC\r\nJOIN USER_CONS_COLUMNS UCC ON TC.TABLE_NAME = UCC.TABLE_NAME\r\nWHERE TC.TABLE_NAME = '" + tableName + "'; ";

                    ResultSet constraintSet = connection.createStatement().executeQuery(constraintSql);
                    while (constraintSet.next()) {
                        if (!constraintSet.isLast()) {
                            ddlBuilder.append(" ,\nCONSTRAINT " + constraintSet.getString(3) + " " + constraintSet.getString(4) + " ( " + constraintSet.getString(5) + " ) ");
                            ddlBuilder.append(", ");
                        } else {
                            ddlBuilder.append(" ,\nCONSTRAINT " + constraintSet.getString(3) + " " + constraintSet.getString(4) + " ( " + constraintSet.getString(5) + " ) ");
                        }
                    }

                    String tableSql = "SELECT \r\nUT.TABLE_SCHEMA, \r\nUT.TABLE_NAME,\r\nUT.TABLESPACE_NAME,\r\nUT.PCT_FREE,\r\nUT.PCT_USED,\r\nUT.INI_TRANS,\r\nUT.MAX_TRANS,\r\nUT.INITIAL_EXTENT*TBS.EXTENT_SIZE,\r\nUT.NEXT_EXTENT*TBS.EXTENT_SIZE,\r\nUT.MIN_EXTENTS*TBS.EXTENT_SIZE,\r\nUT.MAX_EXTENTS*TBS.EXTENT_SIZE\r\nFROM USER_TABLES UT \r\nJOIN V$TABLESPACE TBS ON TBS.TBS_NAME = UT.TABLESPACE_NAME \r\nWHERE UT.TABLE_NAME = '" + tableName + "'; ";
                    ResultSet tablespaceSet = connection.createStatement().executeQuery(tableSql);
                    while (tablespaceSet.next()) {
                        if (tablespaceSet.isLast()) {
                            ddlBuilder.append(" ) \nPCTFREE " + tablespaceSet.getLong(4));
                            ddlBuilder.append(" \nPCTUSED " + tablespaceSet.getLong(5));
                            ddlBuilder.append(" \nINITRANS " + tablespaceSet.getLong(6));
                            ddlBuilder.append(" \nMAXTRANS " + tablespaceSet.getLong(7));
                            ddlBuilder.append(" \nSTORAGE \n( \nINITIAL " + tablespaceSet.getLong(8));
                            ddlBuilder.append(" \nNEXT " + tablespaceSet.getLong(9));
                            ddlBuilder.append(" \nMINSIZE " + tablespaceSet.getLong(10));
                            ddlBuilder.append(" \nMAXSIZE " + tablespaceSet.getLong(11));
                            ddlBuilder.append(" \n) \nTABLESPACE " + tablespaceSet.getString(3) + " ; ");
                        }
                    }
                    ddl = ddlBuilder.toString();
                    break;
                }

                Scale = 0;
                Integer Scale1 = 0;
                ddlBuilder.append(rsMetaData.getColumnName(i) + " ");
                ddlBuilder.append(rsMetaData.getColumnTypeName(i));
                Scale1 = rsMetaData.getPrecision(i);
                Scale = rsMetaData.getScale(i);
                if (Scale1 != null && Scale1 != 0) {
                    ddlBuilder.append("(").append(Scale1);
                    if (rsMetaData.getColumnTypeName(i) == "NUMBER") {
                        ddlBuilder.append(",").append(Scale).append(") ,");
                    } else {
                        ddlBuilder.append(")").append(" ,");
                    }
                } else {
                    ddlBuilder.append(" ,");
                }

                ++i;
            }

            // index
            String indexNameSql = "select INDEX_SCHEMA, INDEX_NAME, TABLE_SCHEMA, TABLE_NAME, UNIQUENESS from ALL_INDEXES where table_schema = '" + schemaName + "' and table_name= '" + tableName + "'";
            ResultSet indexNameResultSet = connection.createStatement().executeQuery(indexNameSql);
            while (indexNameResultSet.next()) {
                String querySql = "SELECT COLS.INDEX_SCHEMA,COLS.INDEX_NAME,COLS.TABLE_SCHEMA,COLS.TABLE_NAME,COLS.COLUMN_NAME,COLS.DESCEND,COLS.NULL_ORDER,IDX.UNIQUENESS,IDX.PCT_FREE,IDX.INI_TRANS,IDX.MAX_TRANS,IDX.INITIAL_EXTENT*TBS.EXTENT_SIZE,IDX.NEXT_EXTENT*TBS.EXTENT_SIZE,IDX.MIN_EXTENTS*TBS.EXTENT_SIZE,IDX.MAX_EXTENTS*TBS.EXTENT_SIZE,IDX.TABLESPACE_NAME FROM USER_IND_COLUMNS AS COLS JOIN USER_INDEXES AS IDX ON COLS.INDEX_NAME  = IDX.INDEX_NAME JOIN V$TABLESPACE AS TBS ON TBS.TBS_NAME = IDX.TABLESPACE_NAME WHERE COLS.TABLE_NAME = '" + tableName + "' " + "AND COLS.INDEX_NAME = '" + indexNameResultSet.getString("INDEX_NAME") + "' " + "ORDER BY COLS.COLUMN_POSITION;";
                ResultSet indexResultSet = connection.createStatement().executeQuery(querySql);
                ddlBuilder.append("\nCREATE ");
                if (indexNameResultSet.getString("UNIQUENESS").equals("UNIQUE")) {
                    ddlBuilder.append("UNIQUE INDEX " + getIndexName(indexNameResultSet.getString("INDEX_SCHEMA"), indexNameResultSet.getString("INDEX_NAME")) + " ");
                } else {
                    ddlBuilder.append("INDEX " + getIndexName(indexNameResultSet.getString("INDEX_SCHEMA"), indexNameResultSet.getString("INDEX_NAME")) + " ");
                }

                ddlBuilder.append("ON " + indexNameResultSet.getString("TABLE_SCHEMA") + "." + indexNameResultSet.getString("TABLE_NAME") + " \n( ");

                while (indexResultSet.next()) {

                    ddlBuilder.append(indexResultSet.getString(5) + " ");
                    ddlBuilder.append(indexResultSet.getString(6) + " ");
                    ddlBuilder.append(indexResultSet.getString(7));
                    if (!indexResultSet.isLast()) {
                        ddlBuilder.append(", ");
                    } else {
                        ddlBuilder.append(" ) \nPCTFREE " + indexResultSet.getLong(9));
                        ddlBuilder.append(" \nINITRANS " + indexResultSet.getLong(10));
                        ddlBuilder.append(" \nMAXTRANS " + indexResultSet.getLong(11));
                        ddlBuilder.append(" \nSTORAGE \n( \nINITIAL " + indexResultSet.getLong(12));
                        ddlBuilder.append(" \nNEXT " + indexResultSet.getLong(13));
                        ddlBuilder.append(" \nMINSIZE " + indexResultSet.getLong(14));
                        ddlBuilder.append(" \nMAXSIZE " + indexResultSet.getLong(15));
                        ddlBuilder.append(" \n) \nTABLESPACE " + indexResultSet.getString(16) + ";\n");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            return ddlBuilder.toString();
        }
    }

    private String getIndexName(String indexSchema, String indexName) {
        if (indexName.contains("PRIMARY_KEY_INDEX")) {
            return indexSchema + "." + indexName;
        } else {
            return indexSchema + ".\"" + indexName + "\"";
        }
    }

    private String ALL_PROCEDURES_SQL = "select OBJECT_NAME from ALL_PROCEDURES where owner = '%s' and schema_name = '%s' and OBJECT_TYPE = '%s' order by OBJECT_NAME";

    @Override
    public List<Function> functions(Connection connection, String databaseName, String schemaName) {
        List<Function> functions = new ArrayList<>();
        String userName = "";
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = String.format(ALL_PROCEDURES_SQL, userName, schemaName, SUNDBObjectTypeEnum.FUNCTION.getObjectType());
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Function function = new Function();
                function.setDatabaseName(databaseName);
                function.setSchemaName(schemaName);
                function.setFunctionName(resultSet.getString("OBJECT_NAME"));
                functions.add(function);
            }
            return functions;
        });
    }

    private static String ALL_SOURCE_SQL
            = "select text from all_source where TYPE = '%s' and owner = '%s' and schema_name = '%s' and name = '%s'";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
                             String functionName) {
        String userName = "";
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = String.format(ALL_SOURCE_SQL, SUNDBObjectTypeEnum.FUNCTION.getObjectType(), userName, schemaName, functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setFunctionBody(resultSet.getString("text") + "\n");
            }
            return function;
        });

    }

    @Override
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        String userName = "";
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = String.format(ALL_PROCEDURES_SQL, userName, schemaName, SUNDBObjectTypeEnum.PROCEDURE.getObjectType());
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            ArrayList<Procedure> procedures = new ArrayList<>();
            Procedure procedure = new Procedure();
            while (resultSet.next()) {
                procedure.setProcedureName(resultSet.getString("OBJECT_NAME"));
                procedures.add(procedure);
            }
            return procedures;
        });
    }


    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
                               String procedureName) {
        String userName = "";
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = String.format(ALL_SOURCE_SQL, SUNDBObjectTypeEnum.PROCEDURE.getObjectType(), userName, schemaName, procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                sb.append(resultSet.getString("TEXT") + "\n");
            }
            Procedure procedure = new Procedure();
            procedure.setDatabaseName(databaseName);
            procedure.setSchemaName(schemaName);
            procedure.setProcedureName(procedureName);
            procedure.setProcedureBody(sb.toString());
            return procedure;
        });
    }

    private static String TRIGGER_SQL
            = "SELECT OWNER, TRIGGER_NAME, TABLE_OWNER, TABLE_NAME, TRIGGERING_TYPE, TRIGGERING_EVENT, STATUS, TRIGGER_BODY "
            + "FROM ALL_TRIGGERS WHERE OWNER = '%s' AND TRIGGER_NAME = '%s'";

    private static String TRIGGER_SQL_LIST = "SELECT OWNER, TRIGGER_NAME FROM ALL_TRIGGERS WHERE OWNER = '%s'";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        return null;
        /*List<Trigger> triggers = new ArrayList<>();
        String sql = String.format(TRIGGER_SQL_LIST, schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setTriggerName(resultSet.getString("TRIGGER_NAME"));
                trigger.setSchemaName(schemaName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });*/
    }

    @Override
    public Trigger trigger(Connection connection, @NotEmpty String databaseName, String schemaName,
                           String triggerName) {

        /*String sql = String.format(TRIGGER_SQL, schemaName, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("TRIGGER_BODY"));
            }
            return trigger;
        });*/
        return null;
    }

    private static String VIEW_SQL
            = "SELECT OWNER, VIEW_NAME, TEXT FROM ALL_VIEWS WHERE OWNER = '%s' AND VIEW_NAME = '%s'";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("TEXT"));
            }
            return table;
        });
    }

    private static String INDEX_SQL = "SELECT i.TABLE_NAME, i.INDEX_TYPE, i.INDEX_NAME, i.UNIQUENESS ,c.COLUMN_NAME, c.COLUMN_POSITION, c.DESCEND, cons.CONSTRAINT_TYPE FROM ALL_INDEXES i JOIN ALL_IND_COLUMNS c ON i.INDEX_NAME = c.INDEX_NAME AND i.TABLE_NAME = c.TABLE_NAME AND i.TABLE_OWNER = c.TABLE_OWNER LEFT JOIN ALL_CONSTRAINTS cons ON i.INDEX_NAME = cons.INDEX_NAME AND i.TABLE_NAME = cons.TABLE_NAME AND i.TABLE_OWNER = cons.OWNER WHERE i.TABLE_OWNER = '%s' AND i.TABLE_NAME = '%s' ORDER BY i.INDEX_NAME, c.COLUMN_POSITION;";

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(INDEX_SQL, schemaName, tableName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            LinkedHashMap<String, TableIndex> map = new LinkedHashMap();
            while (resultSet.next()) {
                String keyName = resultSet.getString("INDEX_NAME");
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
                    index.setUnique("UNIQUE".equalsIgnoreCase(resultSet.getString("UNIQUENESS")));
//                    index.setType(resultSet.getString("Index_type"));
//                    index.setComment(resultSet.getString("Index_comment"));
                    List<TableIndexColumn> tableIndexColumns = new ArrayList<>();
                    tableIndexColumns.add(getTableIndexColumn(resultSet));
                    index.setColumnList(tableIndexColumns);
                    if ("P".equalsIgnoreCase(resultSet.getString("CONSTRAINT_TYPE"))) {
                        index.setType(SUNDBIndexTypeEnum.PRIMARY_KEY.getName());
                    } else if (index.getUnique()) {
                        index.setType(SUNDBIndexTypeEnum.UNIQUE.getName());
                    } else if ("BITMAP".equalsIgnoreCase(resultSet.getString("INDEX_TYPE"))) {
                        index.setType(SUNDBIndexTypeEnum.BITMAP.getName());
                    } else {
                        index.setType(SUNDBIndexTypeEnum.NORMAL.getName());
                    }
                    map.put(keyName, index);
                }
            }
            return map.values().stream().collect(Collectors.toList());
        });

    }

    private TableIndexColumn getTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("COLUMN_NAME"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("COLUMN_POSITION"));
//        tableIndexColumn.setCollation(resultSet.getString("Collation"));
//        tableIndexColumn.setCardinality(resultSet.getLong("Cardinality"));
//        tableIndexColumn.setSubPart(resultSet.getLong("Sub_part"));
        String collation = resultSet.getString("DESCEND");
        if ("ASC".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("ASC");
        } else if ("DESC".equalsIgnoreCase(collation)) {
            tableIndexColumn.setAscOrDesc("DESC");
        }
        return tableIndexColumn;
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new SUNDBSqlBuilder();
    }

    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(SUNDBColumnTypeEnum.getTypes())
                .charsets(Lists.newArrayList())
                .collations(Lists.newArrayList())
                .indexTypes(SUNDBIndexTypeEnum.getIndexTypes())
                .defaultValues(SUNDBDefaultValueEnum.getDefaultValues())
                .build();
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "\"" + name + "\"").collect(Collectors.joining("."));
    }


    @Override
    public List<String> getSystemSchemas() {
        return systemSchemas;
    }

}
