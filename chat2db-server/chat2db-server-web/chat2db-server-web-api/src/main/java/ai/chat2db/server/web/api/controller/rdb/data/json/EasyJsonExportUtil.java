package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.rdb.data.option.json.ExportData2JsonOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
                String columnName = metaData.getColumnName(i);
                if (filedNames != null && !filedNames.contains(columnName)) {
                    log.info("{} is not in the export field list", columnName);
                    continue;
                }
                row.put(columnName, resultSet.getObject(i));
            }
            data.add(row);
        }
        return data;
    }

    public static ObjectMapper getObjectMapper(ExportData2JsonOptions jsonExportDataOption) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (!jsonExportDataOption.getIsTimestamps()) {
            String dataTimeFormat = jsonExportDataOption.getDataTimeFormat();
            log.info("configure dataTimeFormat:{}", dataTimeFormat);
            objectMapper.setDateFormat(new SimpleDateFormat(dataTimeFormat));
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        return objectMapper;
    }
}