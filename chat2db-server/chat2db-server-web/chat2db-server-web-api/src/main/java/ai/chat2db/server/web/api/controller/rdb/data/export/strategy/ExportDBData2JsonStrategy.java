package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.data.option.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

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


    @Override
    protected void doTableDataImport(Connection connection, String databaseName, String schemaName,
                                     ImportTableOption importTableOption,
                                     ImportDataOption importDataOption, MultipartFile file) {
        String tableName = importTableOption.getTableName();
        List<String> srcColumnNames = importTableOption.getSrcColumnNames();
        List<String> targetColumnNames = importTableOption.getTargetColumnNames();
        List<String> sqlList = new ArrayList<>();
        String rootNodeName = ((JSONImportDataOption) importDataOption).getRootNodeName();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try (Statement statement = connection.createStatement()) {
            JsonNode jsonNode = objectMapper.readTree(file.getInputStream());
            if (StringUtils.isNotBlank(rootNodeName) && jsonNode.has(rootNodeName)) {
                jsonNode = jsonNode.get(rootNodeName);
            }
            if (!jsonNode.isArray() || jsonNode.size() <= 0) {
                throw new BusinessException("jsonFile.parse.error");
            }
            Iterator<JsonNode> records = jsonNode.elements();
            while (records.hasNext()) {
                JsonNode recordNode = records.next();
                StringBuilder valuesBuilder = new StringBuilder();
                for (int i = 0; i < srcColumnNames.size(); i++) {
                    String columnName = srcColumnNames.get(i);
                    JsonNode columnValueNode = recordNode.get(columnName);
                    String columnValue = (columnValueNode != null) ? columnValueNode.asText() : "NULL";
                    valuesBuilder.append((Objects.equals(columnValue, "NULL")) ? columnValue : ("'" + columnValue + "'"));
                    if (i < srcColumnNames.size() - 1) {
                        valuesBuilder.append(", ");
                    }
                }
                String columns = String.join(", ", targetColumnNames);
                String values = valuesBuilder.toString();
                String sql = buildInsertSql(schemaName, tableName, columns, values);
                sqlList.add(sql);
                if (sqlList.size()>=1000) {
                    statement.addBatch(String.join("; ", sqlList));
                    statement.executeBatch();
                    statement.clearBatch();
                    sqlList.clear();
                }
            }
            if (sqlList.size()>0) {
                statement.addBatch(String.join("; ", sqlList));
                statement.executeBatch();
                statement.clearBatch();
                sqlList.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildInsertSql(String schemaName, String tableName, String columns, String values) {
        if (StringUtils.isNotBlank(schemaName)) {
            return String.format("INSERT INTO %s.%s (%s) VALUES (%s)", schemaName, tableName, columns, values);
        } else {
            return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
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