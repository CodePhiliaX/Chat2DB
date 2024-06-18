package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.sql.BaseExportData2SqlOptions;
import ai.chat2db.spi.util.ResultSetUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: zgq
 * @date: 2024年04月26日 12:46
 */
@Slf4j
public class EasySqlBuilder {

    public static String buildQuerySql(String databaseName, String schemaName, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        buildTableName(databaseName, schemaName, tableName, sqlBuilder);
        return sqlBuilder.toString();
    }

    private static void buildTableName(String databaseName, String schemaName, String tableName, StringBuilder sqlBuilder) {
        if (StringUtils.isNotBlank(databaseName)) {
            sqlBuilder.append(databaseName).append(".");
        }

        if (StringUtils.isNotBlank(schemaName)) {
            sqlBuilder.append(schemaName).append(".");
        }

        if (StringUtils.isNotBlank(tableName)) {
            sqlBuilder.append(tableName);
        }
    }

    public static void exportData2Sql(String databaseName, String schemaName, String tableName, List<String> tableColumns, AbstractExportDataOptions exportDataOption,
                                      ResultSet resultSet, PrintWriter writer) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> headList = ResultSetUtils.getRsHeader(resultSet);
        if (tableColumns.size() != headList.size()) {
            headList = tableColumns;
        }
        String sqlType = ((BaseExportData2SqlOptions) exportDataOption).getSqlType();
        switch (sqlType) {
            case "single" -> {
                log.info("Exporting single insert SQL for table:" + tableName);
                buildSingleInsert(writer, resultSet, metaData, databaseName, schemaName, tableName, headList, exportDataOption);
            }
            case "multi" -> {
                log.info("Exporting multi insert SQL for table: " + tableName);
                buildMultiInsert(writer, resultSet, metaData, databaseName, schemaName, tableName, headList, exportDataOption);
            }
            case "update" -> {
                log.info("Exporting update SQL for table: " + tableName);
                buildUpdateStatement(writer, resultSet, metaData, databaseName, schemaName, tableName, headList, exportDataOption);
            }
            default -> throw new IllegalStateException("Unexpected value: " + sqlType);
        }
    }

    public static void buildUpdateStatement(PrintWriter writer, ResultSet resultSet, ResultSetMetaData metaData,
                                            String databaseName, String schemaName, String tableName, List<String> headList, AbstractExportDataOptions exportDataOption) throws SQLException {
        String updateCondition = ((BaseExportData2SqlOptions) exportDataOption).getUpdateCondition();
        StringBuilder sqlBuilder = new StringBuilder();
        while (resultSet.next()) {
            EasySqlBuilder.buildUpdate(databaseName, schemaName, tableName, sqlBuilder);
            int columnCount = 0;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName) || Objects.equals(updateCondition, columnName)) {
                    continue;
                }
                columnCount++;
                String value = resultSet.getString(i);
                if (Objects.isNull(value)) {
                    sqlBuilder.append(columnName).append(" = NULL");
                } else {
                    sqlBuilder.append(columnName).append(" = '").append(value).append("'");
                }

                if (columnCount < headList.size() - 1) {
                    sqlBuilder.append(", ");

                }
            }
            EasySqlBuilder.buildUpdateConditions(updateCondition, resultSet.getString(updateCondition), sqlBuilder);
        }
        writer.println(sqlBuilder);
    }

    public static void buildMultiInsert(PrintWriter writer, ResultSet resultSet, ResultSetMetaData metaData,
                                        String databaseName, String schemaName, String tableName, List<String> headList, AbstractExportDataOptions exportDataOption) throws SQLException {
        Boolean containsHeader = ((BaseExportData2SqlOptions) exportDataOption).getContainsHeader();
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> values = new ArrayList<>();
        EasySqlBuilder.buildInsert(databaseName, schemaName, tableName, containsHeader, headList, sqlBuilder);
        while (resultSet.next()) {
            int columnCount = 0;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName)) {
                    continue;
                }
                columnCount++;
                String value = resultSet.getString(i);
                if (Objects.isNull(value)) {
                    values.add(null);
                } else {
                    values.add(value);
                }
                if (columnCount == headList.size()) {
                    break;
                }
            }
            EasySqlBuilder.buildInsertValues(values, sqlBuilder);
            if (resultSet.isLast()) {
                sqlBuilder.append(";\n");
                break;
            } else {
                sqlBuilder.append(",\n");
            }
            values.clear();
        }
        writer.println(sqlBuilder);
    }

    public static void buildSingleInsert(PrintWriter writer, ResultSet resultSet, ResultSetMetaData metaData,
                                         String databaseName, String schemaName, String tableName, List<String> headList, AbstractExportDataOptions exportDataOption) throws SQLException {
        Boolean containsHeader = ((BaseExportData2SqlOptions) exportDataOption).getContainsHeader();
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> values = new ArrayList<>();
        while (resultSet.next()) {
            int columnCount = 0;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName)) {
                    continue;
                }
                columnCount++;
                String value = resultSet.getString(i);
                if (Objects.isNull(value)) {
                    values.add(null);
                } else {
                    values.add(value);
                }
                if (columnCount == headList.size()) {
                    break;
                }
            }
            EasySqlBuilder.buildInsert(databaseName, schemaName, tableName, containsHeader, headList, sqlBuilder);
            EasySqlBuilder.buildInsertValues(values, sqlBuilder);
            sqlBuilder.append(";\n");
            values.clear();
        }
        writer.println(sqlBuilder);
    }

    public static void buildInsert(String databaseName, String schemaName, @NotBlank String tableName, boolean containsHeader, List<String> fieldNames, StringBuilder sqlBuilder) {
        sqlBuilder.append("INSERT INTO ");
        buildTableName(databaseName, schemaName, tableName, sqlBuilder);

        if (containsHeader) {
            sqlBuilder.append(" ").append(buildColumns(fieldNames));
        }
        sqlBuilder.append(" VALUES ");
    }


    public static void buildInsertValues(List<String> values, StringBuilder sqlBuilder) {
        sqlBuilder.append(buildValues(values));
    }

    public static void buildUpdate(String databaseName, String schemaName, @NotBlank String tableName, StringBuilder sqlBuilder) {
        sqlBuilder.append("UPDATE ");
        buildTableName(databaseName, schemaName, tableName, sqlBuilder);
        sqlBuilder.append(" SET ");
    }

    public static void buildUpdateConditions(String updateCondition, String value, StringBuilder sqlBuilder) {
        sqlBuilder.append(" WHERE ").append(updateCondition).append(" = ").append("'").append(value).append("'").append(";\n");
    }

    public static String buildValues(List<String> list) {
        return list.stream()
                .map(s -> s == null ? null : "'" + s + "'")
                .collect(Collectors.joining(",", "(", ")"));
    }

    public static String buildColumns(List<String> headers) {
        return headers.stream()
                .collect(Collectors.joining(",", "(", ")"));
    }
}
