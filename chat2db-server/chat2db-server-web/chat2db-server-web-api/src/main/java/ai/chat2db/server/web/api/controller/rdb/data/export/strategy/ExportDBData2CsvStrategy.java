package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.spi.util.ResultSetUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
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

public class ExportDBData2CsvStrategy extends ExportDBDataStrategy  {

    public ExportDBData2CsvStrategy() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "text/csv";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName,
                                               String schemaName, String tableName) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        export2CSV(connection, schemaName, tableName, byteOut);
        return byteOut;
    }

    @Override
    protected void exportData(HttpServletResponse response, Connection connection,
                              String databaseName, String schemaName, String tableName) throws SQLException {
        try {
            export2CSV(connection, schemaName, tableName, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void export2CSV(Connection connection, String schemaName, String tableName, OutputStream byteOut) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            EasyExcel.write(byteOut)
                    .excelType(ExcelTypeEnum.CSV)
                    .sheet(tableName)
                    .head(getHeadList(resultSet))
                    .doWrite(getDataList(resultSet));
        }
    }

    @NotNull
    private List<List<Object>> getDataList(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
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