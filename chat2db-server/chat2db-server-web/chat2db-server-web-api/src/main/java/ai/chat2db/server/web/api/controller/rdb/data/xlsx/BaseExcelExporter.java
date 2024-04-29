package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractDataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月27日 15:23
 */
@Slf4j
public abstract class BaseExcelExporter extends AbstractDataFileExporter {


    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName, List<String> tableColumns,
                                     AbstractExportDataOptions exportDataOption) throws SQLException {
        ExcelTypeEnum excelType = getExcelType();
        log.info("Export File Format {} file", excelType.name());
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            ServletOutputStream outputStream = response.getOutputStream();
            EasyExcelExportUtil.write(outputStream, EasyExcelExportUtil.getDataList(resultSet, tableColumns),
                                      tableName, ResultSetUtils.getRsHeader(resultSet),
                                      tableColumns, excelType, exportDataOption);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                      String tableName, List<String> tableColumns,
                                                      AbstractExportDataOptions exportDataOption) throws SQLException {
        ExcelTypeEnum excelType = getExcelType();
        log.info("Export File Format {} file", excelType.name());
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            EasyExcelExportUtil.write(byteOut, EasyExcelExportUtil.getDataList(resultSet, tableColumns),
                                      tableName, ResultSetUtils.getRsHeader(resultSet),
                                      tableColumns, excelType, exportDataOption);
            return byteOut;
        }
    }


    protected abstract ExcelTypeEnum getExcelType();

}
