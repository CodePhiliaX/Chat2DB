package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.json.ExportData2JsonOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractMultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:22
 */
public class MultiJSONExporter extends AbstractMultiFileExporter implements MultiFileExporter {


    public MultiJSONExporter() {
        suffix = ExportFileSuffix.JSON.getSuffix();
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
