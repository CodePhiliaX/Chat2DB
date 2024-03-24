package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportDBData2JsonStrategy extends ExportDBDataStrategy {

    public ExportDBData2JsonStrategy() {
        suffix = ExportFileSuffix.JSON.getSuffix();
        contentType = "application/zip";
    }

    @Override
    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        String fileName = Objects.isNull(param.getSchemaName()) ? param.getDatabaseName() : param.getSchemaName();

        response.setContentType(getContentType());
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ExportFileSuffix.ZIP.getSuffix());

        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(outputStream);
             Connection connection = Chat2DBContext.getConnection()) {
            List<String> tableNames = Chat2DBContext.getMetaData().tableNames(connection, databaseName, schemaName, null);

            for (String tableName : tableNames) {
                exportTableData(tableName, connection, zipOut);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to export database data", e);
        }
    }

    private void exportTableData(String tableName, Connection connection, ZipOutputStream zipOut) throws IOException, SQLException {
        String jsonFileName = tableName + getSuffix();
        zipOut.putNextEntry(new ZipEntry(jsonFileName));

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            String sql = String.format("SELECT * FROM %s", tableName);

            try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<Map<String, Object>> data = new ArrayList<>();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    data.add(row);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(writer, data);

                byte[] bytes = byteOut.toByteArray();
                zipOut.write(bytes, 0, bytes.length);
                zipOut.closeEntry();
            }
        }
    }
}
