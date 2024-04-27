package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.data.option.json.ExportData2JsonOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:46
 */
public class EasyJsonExportUtil {


    public static void write(List<String> tableColumns, ExportData2JsonOptions exportDataOption, ResultSet resultSet, PrintWriter writer) throws SQLException {
        List<Map<String, Object>> data = EasyJsonExportUtil.getDataMap(resultSet, tableColumns);
        ObjectMapper objectMapper = EasyJsonExportUtil.getObjectMapper(exportDataOption);
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            writer.println(jsonString);
        } catch (IOException e) {
            throw new BusinessException("data.export2Json.error", data.toArray(), e);
        }
    }
    public static List<Map<String, Object>> getDataMap(ResultSet resultSet, List<String> filedNames) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> data = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                if (filedNames != null && !filedNames.contains(metaData.getColumnName(i))) {
                    continue;
                }
                row.put(metaData.getColumnName(i), resultSet.getObject(i));
            }
            data.add(row);
        }
        return data;
    }

    public static ObjectMapper getObjectMapper(ExportData2JsonOptions jsonExportDataOption) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (!jsonExportDataOption.getIsTimestamps()) {
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            SimpleDateFormat dateFormat = new SimpleDateFormat(jsonExportDataOption.getDataTimeFormat());
            objectMapper.setDateFormat(dateFormat);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        return objectMapper;
    }
}
