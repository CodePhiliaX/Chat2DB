package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:50
 */
public class ExportDBData2SqlStrategy extends ExportDBDataStrategy {

    public ExportDBData2SqlStrategy() {
        suffix = ExportFileSuffix.SQL.getSuffix();
        contentType = "application/zip";
    }

    @Override
    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        String fileName = Objects.isNull(schemaName) ? databaseName : schemaName;
        response.setContentType(getContentType());
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ExportFileSuffix.ZIP.getSuffix());
        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(outputStream);) {
            Connection connection = Chat2DBContext.getConnection();
            List<String> tableNames = Chat2DBContext.getMetaData().tableNames(connection, databaseName, schemaName, null);
            for (String tableName : tableNames) {
                String sqlFileName = tableName + getSuffix();
                zipOut.putNextEntry(new ZipEntry(sqlFileName));
                try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                     PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
                    String sql = Chat2DBContext.getDBManage().exportDatabaseData(connection, databaseName, schemaName, tableName);
                    writer.println(sql);
                    writer.flush();
                    byte[] bytes = byteOut.toByteArray();
                    zipOut.write(bytes, 0, bytes.length);
                    zipOut.closeEntry();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
