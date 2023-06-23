package ai.chat2db.plugin.h2;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H2Meta extends DefaultMetaService implements MetaData {
    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
        return getDDL(databaseName, schemaName, tableName);
    }


    private String getDDL(String databaseName, String schemaName, String tableName) {
        try {
            Connection connection = SQLExecutor.getInstance().getConnection();
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
}
