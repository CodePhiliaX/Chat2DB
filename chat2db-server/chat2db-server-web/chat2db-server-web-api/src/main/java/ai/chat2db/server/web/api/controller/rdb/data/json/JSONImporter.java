package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.json.ImportJsonDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractDataFileImporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:20
 */
@Slf4j
public class JSONImporter extends AbstractDataFileImporter implements DataFileImporter {

    @Override
    protected void doImportData(Connection connection, String databaseName, String schemaName, String tableName, List<String> tableColumns,
                                List<String> fileColumns, AbstractImportDataOptions importDataOption, MultipartFile file) {
        log.info("import JSON data file");
        String rootNodeName = ((ImportJsonDataOptions) importDataOption).getRootNodeName();
        String dataTimeFormat = ((ImportJsonDataOptions) importDataOption).getDataTimeFormat();
        Integer dataStartRowNum = ((ImportJsonDataOptions) importDataOption).getDataStartRowNum();
        Integer dataEndRowNum = ((ImportJsonDataOptions) importDataOption).getDataEndRowNum();
        int limitRowSize = dataEndRowNum - dataStartRowNum + 1;
        int sqlCount = 0;
        int recordCount = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder sqlBuilder = new StringBuilder();
        try (Statement statement = connection.createStatement()) {
            JsonNode jsonNode = objectMapper.readTree(file.getInputStream());
            jsonNode = getJsonNode(rootNodeName, jsonNode);
            Iterator<JsonNode> records = jsonNode.elements();
            List<String> values = new ArrayList<>();
            while (records.hasNext() && recordCount++ < limitRowSize) {
                JsonNode recordNode = records.next();
                iteratorValues(fileColumns, dataTimeFormat, values, recordNode);
                addSql(databaseName,schemaName,tableName, fileColumns, sqlBuilder, statement, values);
                sqlCount++;
                if (sqlCount == 1000) {
                    executeBatch(sqlCount, statement);
                    sqlCount = 0;
                }
            }
            if (sqlCount > 0) {
                executeBatch(sqlCount, statement);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeBatch(int sqlCount, Statement statement) throws SQLException {
        log.info("execute batch sqlCount:{}/1000", sqlCount);
        Instant startTime = Instant.now();
        executeBatch(statement);
        log.info("execute batch cost:{}ms", Instant.now().toEpochMilli() - startTime.toEpochMilli());
    }

    private void iteratorValues(List<String> fileColumns, String dataTimeFormat,
                                List<String> values, JsonNode recordNode) throws Exception {
        for (String columnName : fileColumns) {
            JsonNode columnValueNode = recordNode.get(columnName);
            if (Objects.isNull(columnValueNode)) {
                values.add("NULL");
            } else {
                String value = columnValueNode.asText();
                if (isValidDate(value, dataTimeFormat)) {
                    value = formatDate(value, dataTimeFormat);
                }
                values.add(value);
            }
        }
    }

    @NotNull
    private JsonNode getJsonNode(String rootNodeName, JsonNode jsonNode) {
        if (StringUtils.isNotBlank(rootNodeName) && jsonNode.has(rootNodeName)) {
            jsonNode = jsonNode.get(rootNodeName);
        }
        if (!jsonNode.isArray() || jsonNode.size() <= 0) {
            throw new BusinessException("jsonFile.parse.error");
        }
        return jsonNode;
    }

    private static boolean isValidDate(String dateString, String dataTimeFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dataTimeFormat);
            sdf.parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String formatDate(String dateString, String dataTimeFormat) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
        return outputFormat.format(inputFormat.parse(dateString));
    }

    private void executeBatch(Statement statement) throws SQLException {
        statement.executeBatch();
        statement.clearBatch();
    }

    private void addSql(String databaseName, String schemaName, String tableName, List<String> fileColumns, StringBuilder sqlBuilder,
                        Statement statement, List<String> values) throws SQLException {
        EasySqlBuilder.buildInsert(databaseName, schemaName, tableName, true, fileColumns, sqlBuilder);
        EasySqlBuilder.buildInsertValues(values, sqlBuilder);
        sqlBuilder.append(";");
        values.clear();
        statement.addBatch(sqlBuilder.toString());
        sqlBuilder.setLength(0);
    }
}
