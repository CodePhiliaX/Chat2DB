package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.sql.BaseExportData2SqlOptions;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileFactoryProducer;
import ai.chat2db.spi.util.ResultSetUtils;
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

    public static void exportData2Sql(String tableName, List<String> tableColumns, AbstractExportDataOptions exportDataOption,
                                      ResultSet resultSet, PrintWriter writer) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> headList = ResultSetUtils.getRsHeader(resultSet);
        if (tableColumns.size() != headList.size()) {
            headList = tableColumns;
        }
        String sqlType = ((BaseExportData2SqlOptions) exportDataOption).getSqlType();
        switch (sqlType) {
            case "single" -> {
                DataFileFactoryProducer.notifyObservers("Exporting single insert SQL for table:" + tableName);
                buildSingleInsert(writer, resultSet, metaData, tableName, headList, exportDataOption);
            }
            case "multi" -> {
                DataFileFactoryProducer.notifyObservers("Exporting multi insert SQL for table: " + tableName);
                buildMultiInsert(writer, resultSet, metaData, tableName, headList, exportDataOption);
            }
            case "update" -> {
                DataFileFactoryProducer.notifyObservers("Exporting update SQL for table: " + tableName);
                buildUpdateStatement(writer, resultSet, metaData, tableName, headList);
            }
            default -> throw new IllegalStateException("Unexpected value: " + sqlType);
        }
    }

    public static void buildUpdateStatement(PrintWriter writer, ResultSet resultSet, ResultSetMetaData metaData,
                                            String tableName, List<String> headList) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        while (resultSet.next()) {
            EasySqlBuilder.buildUpdate(tableName, sqlBuilder);
            int columnCount = 0;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName) || Objects.equals("id", columnName)) {
                    log.info("{} is not in the export field list", columnName);
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
            EasySqlBuilder.buildUpdateConditions(resultSet.getString("id"), sqlBuilder);
        }
        writer.println(sqlBuilder);
    }

    public static void buildMultiInsert(PrintWriter writer, ResultSet resultSet, ResultSetMetaData metaData,
                                        String tableName, List<String> headList, AbstractExportDataOptions exportDataOption) throws SQLException {
        Boolean containsHeader = ((BaseExportData2SqlOptions) exportDataOption).getContainsHeader();
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> values = new ArrayList<>();
        EasySqlBuilder.buildInsert(tableName, containsHeader, headList, sqlBuilder);
        while (resultSet.next()) {
            int columnCount = 0;
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName)) {
                    log.info("{} is not in the export field list", columnName);
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
                                         String tableName, List<String> headList, AbstractExportDataOptions exportDataOption) throws SQLException {
        Boolean containsHeader = ((BaseExportData2SqlOptions) exportDataOption).getContainsHeader();
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> values = new ArrayList<>();
        while (resultSet.next()) {
            int columnCount = 0;
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (!headList.contains(columnName)) {
                    log.info("{} is not in the export field list", columnName);
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
            EasySqlBuilder.buildInsert(tableName, containsHeader, headList, sqlBuilder);
            EasySqlBuilder.buildInsertValues(values, sqlBuilder);
            sqlBuilder.append(";\n");
            values.clear();
        }
        writer.println(sqlBuilder);
    }

    public static void buildInsert(String tableName, boolean containsHeader, List<String> filedNames, StringBuilder sqlBuilder) {
        sqlBuilder.append("INSERT INTO ").append(tableName);
        if (containsHeader) {
            sqlBuilder.append(" ").append(buildColumns(filedNames));
        }
        sqlBuilder.append(" VALUES ");
    }

    public static void buildInsertValues(List<String> values, StringBuilder sqlBuilder) {
        sqlBuilder.append(buildValues(values));
    }

    public static void buildUpdate(String tableName, StringBuilder sqlBuilder) {
        sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");
    }

    public static void buildUpdateConditions(String value, StringBuilder sqlBuilder) {
        sqlBuilder.append(" WHERE id = ").append("'").append(value).append("'").append(";\n");
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
