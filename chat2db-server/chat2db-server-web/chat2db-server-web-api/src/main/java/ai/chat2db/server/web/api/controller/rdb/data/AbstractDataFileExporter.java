package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.table.BaseTableOptions;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

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
 * @date: 2024年04月27日 15:17
 */
@Slf4j
public abstract class AbstractDataFileExporter implements DataFileExporter {


    public String suffix;
    public String contentType;


    @Override
    public void exportDataFile(DatabaseExportDataParam param, HttpServletResponse response) throws IOException, SQLException {
        if (param.getExportTableOptions().size() > 1) {
            log.info("export multi table data file");
            exportMultiDataFile(param, response);
        } else {
            log.info("export single table data file");
            exportSingleDataFile(param, response);
        }
        log.info("Finished successfully");
    }


    public void exportSingleDataFile(DatabaseExportDataParam param, HttpServletResponse response) throws SQLException {
        BaseTableOptions tableOptions = param.getExportTableOptions().get(0);
        String tableName = tableOptions.getTableName();
        List<String> tableColumns = tableOptions.getTableColumns();
        String schemaName = param.getSchemaName();
        setResponseHeaders(tableName, response);
        doTableDataExport(response, Chat2DBContext.getConnection(), param.getDatabaseName(),
                          schemaName, tableName, tableColumns, param.getExportDataOption());
    }

    public void exportMultiDataFile(DatabaseExportDataParam param, HttpServletResponse response) throws IOException, SQLException {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        setResponseHeaders(databaseName, schemaName, response);
        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (BaseTableOptions exportTableOption : param.getExportTableOptions()) {
            String tableName = exportTableOption.getTableName();
            String fileName = tableName + suffix;
            List<String> tableColumns = exportTableOption.getTableColumns();
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            ByteArrayOutputStream byteOut = doTableDataExport(Chat2DBContext.getConnection(), databaseName, schemaName,
                                                              tableName, tableColumns, param.getExportDataOption());
            byteOut.writeTo(zipOutputStream);
            zipOutputStream.closeEntry();
            byteOut.close();
        }
    }
    protected abstract void doTableDataExport(HttpServletResponse response, Connection connection,
                                              String databaseName, String schemaName, String tableName,
                                              List<String> tableColumns, AbstractExportDataOptions exportDataOption) throws SQLException;

    private void setResponseHeaders(String tableName, HttpServletResponse response) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=" + tableName + suffix);
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
