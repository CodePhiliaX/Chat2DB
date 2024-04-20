package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.domain.api.param.user.TableExportDataParam;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

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
 * @date: 2024年03月24日 12:46
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ExportDBDataStrategy {

    public String suffix;
    public String contentType = "application/zip";


    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        setResponseHeaders(param, response);
        try (Connection connection = Chat2DBContext.getConnection()) {
            if (param instanceof TableExportDataParam){
                doTableDataExport(((TableExportDataParam) param).getTableName(),response,databaseName,schemaName,connection);
            }else {
                doDbDataExport(response, databaseName, schemaName, connection);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doDbDataExport(HttpServletResponse response, String databaseName,
                                String schemaName, Connection connection) throws IOException, SQLException {
        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(outputStream);
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
    }

    private void doTableDataExport(String tableName, HttpServletResponse response, String databaseName,
                                   String schemaName, Connection connection) throws SQLException {
        exportData(response, connection, databaseName, schemaName, tableName);
    }

    private void setResponseHeaders(DatabaseExportDataParam param, HttpServletResponse response) {
        if (param instanceof TableExportDataParam) {
            String tableName = ((TableExportDataParam) param).getTableName();
            response.setContentType(contentType);
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + tableName + suffix);
        } else {
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + getFileName(param) + ExportFileSuffix.ZIP.getSuffix());
        }
    }


    protected String getFileName(DatabaseExportDataParam param) {
        return Objects.isNull(param.getSchemaName()) ? param.getDatabaseName() : param.getSchemaName();
    }

    public String buildQuerySql(String schemaName, String tableName) {
        String sql;
        if (Objects.isNull(schemaName)) {
            sql = String.format("select * from %s", tableName);
        } else {
            sql = String.format("select * from %s.%s", schemaName, tableName);
        }
        return sql;
    }

    protected abstract ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException;

    protected abstract void exportData(HttpServletResponse response, Connection connection, String databaseName, String schemaName, String tableName) throws SQLException;

}