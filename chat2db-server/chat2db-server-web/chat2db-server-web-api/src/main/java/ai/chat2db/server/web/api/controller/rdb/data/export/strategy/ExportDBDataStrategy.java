package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.domain.api.param.datasource.DatabaseImportDataParam;
import ai.chat2db.server.tools.common.model.data.option.ExportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ExportTableOption;
import ai.chat2db.server.tools.common.model.data.option.ImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportTableOption;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
        try (Connection connection = Chat2DBContext.getConnection()) {
            if (param.getExportTableOptions().size() == 1) {
                String tableName = param.getExportTableOptions().get(0).getTableName();
                List<String> filedNames = param.getExportTableOptions().get(0).getExportColumnNames();
                doTableDataExport(response, connection, databaseName, schemaName, tableName, filedNames, param.getExportDataOption());
            } else {
                doDbDataExport(response, connection, databaseName, schemaName, param.getExportTableOptions(), param.getExportDataOption());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void doImport(DatabaseImportDataParam param, MultipartFile file) {
        doTableDataImport(Chat2DBContext.getConnection(), param.getDatabaseName(), param.getSchemaName(),
                          param.getImportTableOption(), param.getImportDataOption(), file);

    }

    private void doDbDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                String schemaName, List<ExportTableOption> exportTableOptions,
                                ExportDataOption options) throws IOException, SQLException {
        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(outputStream);
        for (ExportTableOption exportTableOption : exportTableOptions) {
            String tableName = exportTableOption.getTableName();
            String fileName = tableName + getSuffix();
            List<String> filedNames = exportTableOption.getExportColumnNames();
            zipOut.putNextEntry(new ZipEntry(fileName));
            ByteArrayOutputStream byteOut = doTableDataExport(connection, databaseName, schemaName, tableName, filedNames, options);
            byteOut.writeTo(zipOut);
            zipOut.closeEntry();
            byteOut.close();
        }
    }


    private void setResponseHeaders(DatabaseExportDataParam param, HttpServletResponse response) {
        if (param.getExportTableOptions().size() == 1) {
            String tableName = param.getExportTableOptions().get(0).getTableName();
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

    protected abstract void doTableDataImport(Connection connection, String databaseName, String schemaName,
                                              ImportTableOption importTableOption,
                                              ImportDataOption importDataOption, MultipartFile file);

    protected abstract ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName,
                                                               String schemaName, String tableName,
                                                               List<String> filedNames, ExportDataOption options) throws SQLException;

    protected abstract void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                              String schemaName, String tableName,
                                              List<String> filedNames, ExportDataOption options) throws SQLException;

    public void write(String tableName, OutputStream out, List<String> fileNames, ExportDataOption options,
                      ResultSet resultSet, Charset charset, ExcelTypeEnum type) throws SQLException {
        Boolean containsHeader = options.getContainsHeader();
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.write(out)
                .charset(charset)
                .excelType(type).sheet(tableName);
        if (containsHeader) {
            List<String> header = ResultSetUtils.getRsHeader(resultSet);
            if (fileNames.size() != header.size()) {
                excelWriterSheetBuilder.head(getListHeadList(fileNames));
            } else {
                excelWriterSheetBuilder.head(getListHeadList(header));
            }
        }
        excelWriterSheetBuilder.doWrite(getDataList(resultSet, fileNames));
    }


    @NotNull
    public List<List<Object>> getDataList(ResultSet resultSet, List<String> fileNames) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<List<Object>> dataList = new ArrayList<>();
        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                if (fileNames.size() != columnCount && !fileNames.contains(metaData.getColumnName(i))) {
                    continue;
                }
                row.add(resultSet.getString(i));
            }
            dataList.add(row);
        }
        return dataList;
    }

    @NotNull
    public List<List<String>> getListHeadList(List<String> headers) {
        return headers
                .stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }
}