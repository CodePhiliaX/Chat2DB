package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractDataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 15:33
 */
@Slf4j
public class SQLExporter extends AbstractDataFileExporter implements DataFileExporter {

    public SQLExporter() {
        suffix = ExportFileSuffix.SQL.getSuffix();
        contentType = "text/sql";
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection,
                                     String databaseName, String schemaName,
                                     String tableName, List<String> tableColumns,
                                     AbstractExportDataOptions exportDataOption) throws SQLException {
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        log.info("Export File Format SQL file");
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            PrintWriter writer = response.getWriter();
            EasySqlBuilder.exportData2Sql(databaseName,schemaName,tableName, tableColumns, exportDataOption, resultSet, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                      String tableName, List<String> tableColumns,
                                                      AbstractExportDataOptions exportDataOption) throws SQLException {
        log.info("Export File Format SQL file");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            EasySqlBuilder.exportData2Sql(databaseName, schemaName, tableName, tableColumns, exportDataOption, resultSet, writer);
        }
        return byteOut;
    }
}
