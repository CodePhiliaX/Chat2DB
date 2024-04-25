package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.data.option.CSVImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ExportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportTableOption;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ExportDBData2CsvStrategy extends ExportDBDataStrategy {

    public ExportDBData2CsvStrategy() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "text/csv";
    }

    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName,
                                                      String schemaName, String tableName,
                                                      List<String> filedNames, ExportDataOption options) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        export2CSV(connection, schemaName, tableName, byteOut, filedNames, options);
        return byteOut;
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName,
                                     List<String> filedNames, ExportDataOption options) throws SQLException {
        try {
            export2CSV(connection, schemaName, tableName, response.getOutputStream(), filedNames, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doTableDataImport(Connection connection, String databaseName, String schemaName,
                                     ImportTableOption importTableOption,
                                     ImportDataOption importDataOption, MultipartFile file) {
        NoModelDataListener noModelDataListener = new NoModelDataListener(schemaName, importTableOption,
                                                                          importDataOption, Chat2DBContext.getConnection());
        try {
            Integer headerRowNum = ((CSVImportDataOption) importDataOption).getHeaderRowNum();
            EasyExcel.read(file.getInputStream(), noModelDataListener)
                    .sheet()
                    .headRowNumber(headerRowNum)
                    .doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExcelAnalysisException e) {
            if (Objects.equals("超出指定数据行", e.getMessage())) {
                log.info("数据读取完成");
            } else {
                throw new BusinessException("parse.excel.error", null, e);
            }
        }
    }

    private void export2CSV(Connection connection, String schemaName, String tableName, OutputStream out, List<String> fileNames, ExportDataOption options) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            write(tableName, out, fileNames, options, resultSet, StandardCharsets.UTF_8, ExcelTypeEnum.CSV);
        }
    }


}