package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import com.alibaba.excel.EasyExcel;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportDBData2ExcelStrategy extends ExportDBDataStrategy {

    public ExportDBData2ExcelStrategy() {
        suffix = ExportFileSuffix.EXCEL.getSuffix();
        contentType = "application/zip";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException {
        String sql;
        if (Objects.isNull(schemaName)) {
            sql = String.format("select * from %s", tableName);
        } else {
            sql = String.format("select * from %s.%s", schemaName, tableName);
        }
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<List<String>> headList = new ArrayList<>(columnCount);
            List<List<Object>> dataList = new ArrayList<>();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                    headList.add(List.of(metaData.getColumnName(i)));
                }
                dataList.add(row);
            }
            EasyExcel.write(byteOut).sheet(tableName).head(headList).doWrite(dataList);
        }
        return byteOut;
    }
}
