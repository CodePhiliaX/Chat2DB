package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.export.data.option.ExportDataOption;
import ai.chat2db.server.tools.common.model.export.data.option.SQLExportDataOption;
import ai.chat2db.spi.util.ResultSetUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:50
 */
public class ExportDBData2SqlStrategy extends ExportDBDataStrategy {

    public ExportDBData2SqlStrategy() {
        suffix = ExportFileSuffix.SQL.getSuffix();
        contentType = "text/sql";
    }

    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName,
                                                      String schemaName, String tableName, List<String> filedNames, ExportDataOption options) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            writer.println(export2InsertSQL(connection, schemaName, tableName, filedNames, options));
        }
        return byteOut;
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName, List<String> filedNames,
                                     ExportDataOption options) throws SQLException {
        try {
            response.getWriter().println(export2InsertSQL(connection, schemaName, tableName, filedNames, options));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String export2InsertSQL(Connection connection, String schemaName,
                                    String tableName, List<String> filedNames, ExportDataOption options) throws SQLException {

        StringBuilder sqlBuilder = new StringBuilder();
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<String> headList = ResultSetUtils.getRsHeader(resultSet);
            if (((SQLExportDataOption) options).getToUpdate()) {
                buildUpdateSQL(tableName, filedNames, options, sqlBuilder, resultSet, metaData, headList);
            } else {
                buildInsertSQL(tableName, filedNames, options, sqlBuilder, resultSet, metaData, headList);
            }
            sqlBuilder.append("\n");
        }
        return sqlBuilder.toString();
    }

    private void buildInsertSQL(String tableName, List<String> filedNames, ExportDataOption options,
                                StringBuilder sqlBuilder, ResultSet resultSet, ResultSetMetaData metaData,
                                List<String> headList) throws SQLException {
        boolean containsHeader = options.getContainsHeader();
        Boolean multipleRow = ((SQLExportDataOption) options).getMultipleRow();
        String newTableName = ((SQLExportDataOption) options).getNewTableName();
        if (multipleRow) {
            if (headList.size() != filedNames.size() && StringUtils.isNotBlank(newTableName)) {
                buildInsert(newTableName, containsHeader, filedNames, sqlBuilder);
            } else if (StringUtils.isNotBlank(newTableName)) {
                buildInsert(newTableName, containsHeader, headList, sqlBuilder);
            } else if (headList.size() != filedNames.size()) {
                buildInsert(tableName, containsHeader, filedNames, sqlBuilder);
            } else {
                buildInsert(tableName, containsHeader, headList, sqlBuilder);
            }
        }


        while (resultSet.next()) {
            int filedSeparatorCount = 0;
            if (!multipleRow) {
                if (headList.size() != filedNames.size() && StringUtils.isNotBlank(newTableName)) {
                    buildInsert(newTableName, containsHeader, filedNames, sqlBuilder);
                } else if (StringUtils.isNotBlank(newTableName)) {
                    buildInsert(newTableName, containsHeader, headList, sqlBuilder);
                } else if (headList.size() != filedNames.size()) {
                    buildInsert(tableName, containsHeader, filedNames, sqlBuilder);
                } else {
                    buildInsert(tableName, containsHeader, headList, sqlBuilder);
                }
            }

            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                if (headList.size() != filedNames.size() && !filedNames.contains(metaData.getColumnName(i))) {
                    continue;
                }
                String value = resultSet.getString(i);
                if (i == 1) {
                    sqlBuilder.append("(");
                }
                if (Objects.isNull(value)) {
                    sqlBuilder.append("NULL");
                } else {
                    sqlBuilder.append("'").append(value).append("'");
                }
                filedSeparatorCount++;
                if (headList.size() == filedNames.size()) {
                    if (i < metaData.getColumnCount()) {
                        sqlBuilder.append(",");
                    }
                } else {
                    if (filedSeparatorCount < filedNames.size()) {
                        sqlBuilder.append(",");
                    }
                }
            }
            if (multipleRow) {
                if (resultSet.isLast()) {
                    sqlBuilder.append(");\n");
                    break;
                } else {
                    sqlBuilder.append("),\n");
                }
            } else {
                sqlBuilder.append(");\n");
            }
        }
    }

    private void buildUpdateSQL(String tableName, List<String> fieldNames, ExportDataOption options,
                                StringBuilder sqlBuilder, ResultSet resultSet, ResultSetMetaData metaData,
                                List<String> headList) throws SQLException {

        String newTableName = ((SQLExportDataOption) options).getNewTableName();
        while (resultSet.next()) {

            if (StringUtils.isNotBlank(newTableName)) {
                buildUpdate(newTableName, sqlBuilder);
            } else {
                buildUpdate(tableName, sqlBuilder);
            }
            int fieldSeparatorCount = 0;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);

                if (headList.size() != fieldNames.size() && !fieldNames.contains(columnName)) {
                    continue;
                }
                String value = resultSet.getString(i);
                if (Objects.isNull(value)) {
                    sqlBuilder.append(columnName).append(" = NULL");
                } else {
                    sqlBuilder.append(columnName).append(" = '").append(value).append("'");
                }
                fieldSeparatorCount++;
                if (headList.size() == fieldNames.size()) {
                    if (i < metaData.getColumnCount()) {
                        sqlBuilder.append(",");
                    }
                } else {
                    if (fieldSeparatorCount < fieldNames.size()) {
                        sqlBuilder.append(",");
                    }
                }
            }
            sqlBuilder.append(" WHERE id = ").append("'").append(resultSet.getString("id")).append("'").append(";\n");
        }
    }


    private void buildUpdate(String newTableName, StringBuilder sqlBuilder) {
        sqlBuilder.append("UPDATE ").append(newTableName).append(" SET ");
    }


    private void buildInsert(String tableName, boolean containsHeader, List<String> filedNames, StringBuilder sqlBuilder) {
        sqlBuilder.append("INSERT INTO ").append(tableName);
        if (containsHeader) {
            sqlBuilder.append(" ").append(convertList2String(filedNames));
        }
        sqlBuilder.append(" VALUES ");
    }


    private String convertList2String(List<String> list) {
        return list.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(",", "(", ")"));
    }


}