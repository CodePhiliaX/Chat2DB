package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:46
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ExportDBDataStrategy {

    public String suffix;
    public String contentType;

    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        setResponseHeaders(param, response);
        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(outputStream);
             Connection connection = Chat2DBContext.getConnection();) {
            List<String> tableNames = Chat2DBContext.getMetaData().tableNames(connection, databaseName, schemaName, null);
            tableNames.addAll(Chat2DBContext.getMetaData().viewNames(connection, databaseName, schemaName));
            for (String tableName : tableNames) {
                String fileName = tableName + getSuffix();
                zipOut.putNextEntry(new ZipEntry(fileName));
                ByteArrayOutputStream byteOut = exportData(connection, databaseName, schemaName, tableName);
                byteOut.writeTo(zipOut);
                zipOut.closeEntry();
                byteOut.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setResponseHeaders(DatabaseExportDataParam param, HttpServletResponse response) {
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + getFileName(param) + ExportFileSuffix.ZIP.getSuffix());
    }

    protected String getFileName(DatabaseExportDataParam param) {
        return Objects.isNull(param.getSchemaName()) ? param.getDatabaseName() : param.getSchemaName();
    }

    protected abstract ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException;

}