package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.EasyExcelExportUtil;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 13:04
 */
public abstract class BaseSingleExcelExporter extends AbstractSingleFileExporter {

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName, List<String> tableColumns,
                                     AbstractExportDataOptions exportDataOption) throws SQLException {
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            ServletOutputStream outputStream = response.getOutputStream();
            EasyExcelExportUtil.write(outputStream, EasyExcelExportUtil.getDataList(resultSet, tableColumns),
                                      tableName, ResultSetUtils.getRsHeader(resultSet),
                                      tableColumns, getExcelType(), exportDataOption);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    protected abstract ExcelTypeEnum getExcelType();

}
