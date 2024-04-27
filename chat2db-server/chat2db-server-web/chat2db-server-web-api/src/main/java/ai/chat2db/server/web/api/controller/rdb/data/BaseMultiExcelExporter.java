package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.EasyExcelExportUtil;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 13:17
 */
public abstract class BaseMultiExcelExporter extends AbstractMultiFileExporter {


    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName, String schemaName,
                                                      String tableName, List<String> tableColumns,
                                                      AbstractExportDataOptions exportDataOption) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String sql = EasySqlBuilder.buildQuerySql(databaseName, schemaName, tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            EasyExcelExportUtil.write(byteOut, EasyExcelExportUtil.getDataList(resultSet, tableColumns),
                                      tableName, ResultSetUtils.getRsHeader(resultSet),
                                      tableColumns, getExcelType(), exportDataOption);
            return byteOut;
        }
    }

    protected abstract ExcelTypeEnum getExcelType();
}
