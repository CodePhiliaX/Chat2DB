package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.json.ExportData2JsonOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractDataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import jakarta.servlet.http.HttpServletResponse;

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
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 14:20
 */
public class JSONExporter extends AbstractDataFileExporter implements DataFileExporter {

    public JSONExporter() {
        suffix = ExportFileSuffix.JSON.getSuffix();
        contentType = "application/json";
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName, List<String> tableColumns,
                                     AbstractExportDataOptions exportDataOption) throws SQLException {
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            EasyJsonExportUtil.write(tableColumns, (ExportData2JsonOptions) exportDataOption, resultSet, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                      String tableName, List<String> tableColumns,
                                                      AbstractExportDataOptions exportDataOption) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            EasyJsonExportUtil.write(tableColumns, (ExportData2JsonOptions) exportDataOption, resultSet, writer);
        }
        return byteOut;
    }
}
