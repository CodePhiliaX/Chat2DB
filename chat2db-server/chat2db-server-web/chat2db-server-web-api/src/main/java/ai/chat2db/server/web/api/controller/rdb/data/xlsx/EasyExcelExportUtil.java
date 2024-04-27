package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.BaseExportDataOptions;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zgq
 * @date: 2024年04月26日 13:31
 */
public class EasyExcelExportUtil {

    public static void write(OutputStream out, List<List<Object>> dataList, String fileName,
                             List<String> srcHeaders,List<String> targetHeaders ,ExcelTypeEnum type,
                             AbstractExportDataOptions exportDataOption) {
        Boolean containsHeader = ((BaseExportDataOptions) exportDataOption).getContainsHeader();
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.write(out).excelType(type).sheet(fileName);
        if (containsHeader) {
            if (srcHeaders.size() != targetHeaders.size()) {
                excelWriterSheetBuilder.head(getListHeadList(targetHeaders));
            } else {
                excelWriterSheetBuilder.head(getListHeadList(srcHeaders));
            }
        }
        excelWriterSheetBuilder.doWrite(dataList);

    }

    @NotNull
    public static List<List<String>> getListHeadList(List<String> headers) {
        return headers
                .stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<List<Object>> getDataList(ResultSet resultSet, List<String> fileNames) throws SQLException {
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
}
