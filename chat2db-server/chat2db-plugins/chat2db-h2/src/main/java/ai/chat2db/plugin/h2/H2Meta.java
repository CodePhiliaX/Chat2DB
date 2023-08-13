package ai.chat2db.plugin.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;

public class H2Meta extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(Connection connection, @NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName) {
        return getDDL(connection, databaseName, schemaName, tableName);
    }

    private String getDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        try {
            // 查询表结构信息
            ResultSet columns = connection.getMetaData().getColumns(databaseName, schemaName, tableName, null);
            List<String> columnDefinitions = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                String remarks = columns.getString("REMARKS");
                String defaultValue = columns.getString("COLUMN_DEF");
                String nullable = columns.getInt("NULLABLE") == ResultSetMetaData.columnNullable ? "NULL" : "NOT NULL";
                StringBuilder columnDefinition = new StringBuilder();
                columnDefinition.append(columnName).append(" ").append(columnType);
                if (columnSize != 0) {
                    columnDefinition.append("(").append(columnSize).append(")");
                }
                columnDefinition.append(" ").append(nullable);
                if (defaultValue != null) {
                    columnDefinition.append(" DEFAULT ").append(defaultValue);
                }
                if (remarks != null) {
                    columnDefinition.append(" COMMENT '").append(remarks).append("'");
                }
                columnDefinitions.add(columnDefinition.toString());
            }

            // 查询表索引信息
            ResultSet indexes = connection.getMetaData().getIndexInfo(databaseName, schemaName, tableName, false,
                false);
            Map<String, List<String>> indexMap = new HashMap<>();
            while (indexes.next()) {
                String indexName = indexes.getString("INDEX_NAME");
                String columnName = indexes.getString("COLUMN_NAME");
                if (indexName != null) {
                    if (!indexMap.containsKey(indexName)) {
                        indexMap.put(indexName, new ArrayList<>());
                    }
                    indexMap.get(indexName).add(columnName);
                }
            }
            StringBuilder createTableDDL = new StringBuilder("CREATE TABLE ");
            createTableDDL.append(tableName).append(" (\n");
            createTableDDL.append(String.join(",\n", columnDefinitions));
            createTableDDL.append("\n);\n");

            System.out.println("DDL建表语句：");
            System.out.println(createTableDDL.toString());

            // 输出索引信息
            System.out.println("\nDDL索引语句：");
            for (Map.Entry<String, List<String>> entry : indexMap.entrySet()) {
                String indexName = entry.getKey();
                List<String> columnList = entry.getValue();
                String indexColumns = String.join(", ", columnList);
                String createIndexDDL = String.format("CREATE INDEX %s ON %s (%s);", indexName, tableName,
                    indexColumns);
                System.out.println(createIndexDDL);
                createTableDDL.append(createIndexDDL);
            }
            return createTableDDL.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String ROUTINES_SQL
        =
        "SELECT SPECIFIC_NAME, ROUTINE_DEFINITION FROM information_schema.routines WHERE "
            + "routine_type = '%s' AND ROUTINE_SCHEMA ='%s'  AND "
            + "routine_name = '%s';";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
        String functionName) {

        String sql = String.format(ROUTINES_SQL, "FUNCTION", databaseName, functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                function.setFunctionBody(resultSet.getString("ROUTINE_DEFINITION"));
            }

            return function;
        });

    }

    private static String TRIGGER_SQL
        = "SELECT TRIGGER_NAME,JAVA_CLASS  FROM INFORMATION_SCHEMA.TRIGGERS where "
        + "TRIGGER_SCHEMA = '%s' AND TRIGGER_NAME = '%s';";

    private static String TRIGGER_SQL_LIST
        = "SELECT TRIGGER_NAME FROM INFORMATION_SCHEMA.TRIGGERS where TRIGGER_CATALOG = '%s' AND TRIGGER_SCHEMA = '%s';";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        String sql = String.format(TRIGGER_SQL_LIST, databaseName,schemaName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setTriggerName(resultSet.getString("TRIGGER_NAME"));
                trigger.setSchemaName(schemaName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });
    }

    @Override
    public Trigger trigger(Connection connection, @NotEmpty String databaseName, String schemaName,
        String triggerName) {

        String sql = String.format(TRIGGER_SQL, databaseName, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("JAVA_CLASS"));
            }
            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
        String procedureName) {
        String sql = String.format(ROUTINES_SQL, "PROCEDURE", databaseName, procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Procedure procedure = new Procedure();
            procedure.setDatabaseName(databaseName);
            procedure.setSchemaName(schemaName);
            procedure.setProcedureName(procedureName);
            if (resultSet.next()) {
                procedure.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
                procedure.setProcedureBody(resultSet.getString("ROUTINE_DEFINITION"));
            }
            return procedure;
        });
    }

    private static String VIEW_SQL
        = "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_CATALOG = '%s' AND TABLE_SCHEMA = '%s' "
        + "AND TABLE_NAME = '%s';";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, databaseName, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("VIEW_DEFINITION"));
            }
            return table;
        });
    }
}
