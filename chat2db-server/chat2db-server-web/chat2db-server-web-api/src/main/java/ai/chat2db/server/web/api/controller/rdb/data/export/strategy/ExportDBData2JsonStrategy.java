package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.data.option.ExportDataOption;
import ai.chat2db.server.tools.common.model.data.option.JSONExportDataOption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportDBData2JsonStrategy extends ExportDBDataStrategy {

    public ExportDBData2JsonStrategy() {
        suffix = ExportFileSuffix.JSON.getSuffix();
        contentType = "application/json";
    }

    @Override
    protected ByteArrayOutputStream doTableDataExport(Connection connection, String databaseName,
                                                      String schemaName, String tableName,
                                                      List<String> filedNames, ExportDataOption options) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            export2JSON(resultSet, writer, filedNames, options);
        }
        return byteOut;
    }

    @Override
    protected void doTableDataExport(HttpServletResponse response, Connection connection, String databaseName,
                                     String schemaName, String tableName,
                                     List<String> filedNames, ExportDataOption options) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(buildQuerySql(schemaName, tableName))) {
            export2JSON(resultSet, response.getWriter(), filedNames, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void export2JSON(ResultSet resultSet, PrintWriter writer, List<String> filedNames, ExportDataOption options) throws SQLException {
        List<Map<String, Object>> data = getDataMap(resultSet, filedNames);
        ObjectMapper objectMapper = new ObjectMapper();
        JSONExportDataOption jsonExportDataOption = (JSONExportDataOption) options;
        if (!jsonExportDataOption.getIsTimestamps()) {
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            SimpleDateFormat dateFormat = new SimpleDateFormat(jsonExportDataOption.getDataTimeFormat());
            objectMapper.setDateFormat(dateFormat);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            writer.println(jsonString);
        } catch (IOException e) {
            throw new BusinessException("data.export2Json.error", data.toArray(), e);
        }
    }

    @NotNull
    private List<Map<String, Object>> getDataMap(ResultSet resultSet, List<String> filedNames) throws SQLException {
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
}