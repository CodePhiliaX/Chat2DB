package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.json.ExportData2JsonOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractSingleFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 14:21
 */
public class SingleJSONExporter extends AbstractSingleFileExporter implements SingleFileExporter {

    public SingleJSONExporter() {
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
}
