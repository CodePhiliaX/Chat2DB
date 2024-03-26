package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportDBData2ExcelStrategy extends ExportDBDataStrategy {

    public ExportDBData2ExcelStrategy() {
        suffix = ExportFileSuffix.EXCEL.getSuffix();
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
             ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {
            Connection connection = Chat2DBContext.getConnection();
            List<String> tableNames = Chat2DBContext.getMetaData().tableNames(connection, databaseName, schemaName, null);
            for (String tableName : tableNames) {
                String excelFileName = tableName + getSuffix();
                zipStream.putNextEntry(new ZipEntry(excelFileName));

                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                String sql = String.format("SELECT * FROM %s", tableName);
                try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<String> rsHeader = ResultSetUtils.getRsHeader(resultSet);
                    List<List<String>> headList = new ArrayList<>();
                    for (String header : rsHeader) {
                        headList.add(List.of(header));
                    }
                    List<List<Object>> dataList = new ArrayList<>();
                    while (resultSet.next()) {
                        List<Object> col = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            col.add(resultSet.getString(i));
                        }
                        dataList.add(col);
                    }
                    EasyExcel.write(byteOut).head(headList).sheet(tableName).doWrite(dataList);
                    byte[] bytes = byteOut.toByteArray();
                    zipStream.write(bytes, 0, bytes.length);
                    zipStream.closeEntry();
                }

            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
