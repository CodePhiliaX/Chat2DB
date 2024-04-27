package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractMultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;

import java.io.ByteArrayOutputStream;
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
 * @date: 2024年04月26日 15:42
 */
public class MultiSQLExporter extends AbstractMultiFileExporter implements MultiFileExporter {

    public MultiSQLExporter() {
        suffix = ExportFileSuffix.SQL.getSuffix();
    }
    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                      String tableName, List<String> tableColumns,
                                                      AbstractExportDataOptions exportDataOption) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            EasySqlBuilder.exportData2Sql(tableName, tableColumns, exportDataOption, resultSet, writer);
        }
        return byteOut;
    }
}
