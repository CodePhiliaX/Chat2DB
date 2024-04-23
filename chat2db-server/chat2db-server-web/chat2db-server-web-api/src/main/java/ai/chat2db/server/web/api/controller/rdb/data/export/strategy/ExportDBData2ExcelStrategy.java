package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.common.model.data.option.ExportDataOption;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ExportDBData2ExcelStrategy extends ExportDBDataStrategy {

    public ExportDBData2ExcelStrategy() {
        suffix = ExportFileSuffix.EXCEL.getSuffix();
        contentType = "application/vnd.ms-excel";
    }

    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName,
                                                      String schemaName, String tableName,
                                                      List<String> filedNames, ExportDataOption options) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        export2EXCEL(connection, schemaName, tableName, byteOut, filedNames, options);
        return byteOut;
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName,
                                     List<String> filedNames, ExportDataOption options) throws SQLException {
        try {
            export2EXCEL(connection, schemaName, tableName, response.getOutputStream(), filedNames, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void export2EXCEL(Connection connection, String schemaName, String tableName, OutputStream out, List<String> fileNames, ExportDataOption options) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            write(tableName, out, fileNames, options, resultSet, StandardCharsets.UTF_8, ExcelTypeEnum.XLSX);
        }
    }


}