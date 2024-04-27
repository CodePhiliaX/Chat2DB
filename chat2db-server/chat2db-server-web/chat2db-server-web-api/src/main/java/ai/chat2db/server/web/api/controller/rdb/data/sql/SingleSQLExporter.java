package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractSingleFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 15:41
 */
public class SingleSQLExporter extends AbstractSingleFileExporter implements SingleFileExporter {


    public SingleSQLExporter() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "text/sql";
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection,
                                     String databaseName, String schemaName,
                                     String tableName, List<String> tableColumns,
                                     AbstractExportDataOptions exportDataOption) throws SQLException {
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            PrintWriter writer = response.getWriter();
            EasySqlBuilder.exportData2Sql(tableName, tableColumns, exportDataOption, resultSet, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
