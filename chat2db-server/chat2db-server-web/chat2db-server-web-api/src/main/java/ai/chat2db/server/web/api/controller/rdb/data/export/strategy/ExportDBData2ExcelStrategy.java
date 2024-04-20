package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExportDBData2ExcelStrategy extends ExportDBDataStrategy {

    public ExportDBData2ExcelStrategy() {
        suffix = ExportFileSuffix.EXCEL.getSuffix();
        contentType = "application/vnd.ms-excel";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName,
                                               String schemaName, String tableName) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        export2EXCEL(connection, schemaName, tableName, byteOut);
        return byteOut;
    }

    @Override
    protected void exportData(HttpServletResponse response, Connection connection, String databaseName,
                              String schemaName, String tableName) throws SQLException {
        try  {
            export2EXCEL(connection, schemaName, tableName, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void export2EXCEL(Connection connection, String schemaName, String tableName, OutputStream byteOut) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<List<String>> headList = getHeadList(resultSet);
            List<List<Object>> dataList = getDataList(resultSet, columnCount);
            EasyExcel.write(byteOut).sheet(tableName).head(headList).doWrite(dataList);
        }
    }

    @NotNull
    private List<List<Object>> getDataList(ResultSet resultSet, int columnCount) throws SQLException {
        List<List<Object>> dataList = new ArrayList<>();
        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(resultSet.getString(i));
            }
            dataList.add(row);
        }
        return dataList;
    }

    @NotNull
    private List<List<String>> getHeadList(ResultSet resultSet) {
        return ResultSetUtils.getRsHeader(resultSet)
                .stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }
}