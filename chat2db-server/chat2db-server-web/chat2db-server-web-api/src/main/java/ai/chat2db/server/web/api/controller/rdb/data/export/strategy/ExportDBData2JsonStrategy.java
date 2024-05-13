package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.tools.base.excption.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ExportDBData2JsonStrategy extends ExportDBDataStrategy {

    public ExportDBData2JsonStrategy() {
        suffix = ExportFileSuffix.JSON.getSuffix();
        contentType = "application/zip";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            String sql;
            if (Objects.isNull(schemaName)) {
                sql = String.format("SELECT * FROM %s", tableName);
            } else {
                sql = String.format("SELECT * FROM %s.%s", schemaName, tableName);
            }
            try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<Map<String, Object>> data = new ArrayList<>();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    data.add(row);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    String jsonString = objectMapper.writeValueAsString(data);
                    writer.println(jsonString);
                } catch (IOException e) {
                    throw new BusinessException("data.export2Json.error",data.toArray(),e);
                }
            }
        }
        return byteOut;
    }


}