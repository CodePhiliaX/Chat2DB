package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.table.BaseTableOptions;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:41
 */
@Data
public abstract class AbstractMultiFileExporter implements MultiFileExporter {

     public String suffix;

    @Override
    public void doMultiFileExport(DatabaseExportDataParam param, HttpServletResponse response) throws IOException, SQLException {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        setResponseHeaders(databaseName, schemaName, response);
        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (BaseTableOptions exportTableOption : param.getExportTableOptions()) {
            String tableName = exportTableOption.getTableName();
            String fileName = tableName + getSuffix();
            List<String> tableColumns = exportTableOption.getTableColumns();
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            ByteArrayOutputStream byteOut = doTableDataExport(Chat2DBContext.getConnection(), databaseName, schemaName,
                                                              tableName, tableColumns, param.getExportDataOption());
            byteOut.writeTo(zipOutputStream);
            zipOutputStream.closeEntry();
            byteOut.close();
        }
    }

    protected abstract ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                    String tableName, List<String> tableColumns,
                                                    AbstractExportDataOptions exportDataOption) throws SQLException;

    private void setResponseHeaders(String databaseName, String schemaName, HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + getFileName(databaseName, schemaName) + ExportFileSuffix.ZIP.getSuffix());
    }

    private String getFileName(String databaseName, String schemaName) {
        return Objects.isNull(schemaName) ? databaseName : schemaName;
    }

}
