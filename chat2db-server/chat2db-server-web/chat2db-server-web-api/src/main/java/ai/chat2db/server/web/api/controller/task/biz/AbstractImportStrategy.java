package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.web.api.controller.task.request.FieldMapping;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractImportStrategy implements ImportStrategy {

    @Override
    public void importData(File file, ImportContext importContext) {
        final AtomicInteger processedCount = new AtomicInteger(0);
        final List<String> fileHeaders = new ArrayList<>();

        try {
            // REPLACE 模式：先清空表
            if ("REPLACE".equals(importContext.getImportMode())) {
                truncateTable(importContext);
            }
        } catch (SQLException e) {
            log.error("truncate table error", e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }

        try (PreparedStatement ps = prepareStatement(importContext)) {

            EasyExcel.read(file, new ReadListener<Map<Integer, String>>() {

                @Override
                public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                    for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                        fileHeaders.add(entry.getValue().getStringValue().trim());
                    }

                    validateMappings(fileHeaders, importContext);

                    log.info("import file headers: {}, target columns: {}", fileHeaders, importContext.getHeaderList());
                }

                @Override
                public void invoke(Map<Integer, String> data, AnalysisContext context) {
                    try {
                        Map<String, String> sourceData = new LinkedHashMap<>();
                        for (Map.Entry<Integer, String> entry : data.entrySet()) {
                            if (entry.getKey() < fileHeaders.size()) {
                                sourceData.put(fileHeaders.get(entry.getKey()), entry.getValue());
                            }
                        }

                        Map<String, String> rowData = convertToTargetData(sourceData, importContext);

                        setParameters(ps, rowData, importContext);
                        ps.addBatch();

                        int count = processedCount.incrementAndGet();
                        if (count % 200 == 0) {
                            ps.executeBatch();
                            ps.clearBatch();
                            importContext.getProgressUpdater().accept(count);
                        }
                    } catch (SQLException e) {
                        log.error("import data batch error at row {}, data: {}", processedCount.get() + 1, data, e);
                        throw new BusinessException("dataSource.importRowError",
                                new Object[]{processedCount.get() + 1, e.getMessage()}, e);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    try {
                        ps.executeBatch();
                        ps.clearBatch();
                        importContext.getProgressUpdater().accept(processedCount.get());
                    } catch (SQLException e) {
                        log.error("import data final batch error", e);
                        throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
                    }
                }
            }).sheet().doRead();
        } catch (SQLException e) {
            log.error("import data error, strategy: {}", this.getClass().getSimpleName(), e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }
    }

    private void setParameters(PreparedStatement ps, Map<String, String> rowData, ImportContext importContext)
            throws SQLException {
        String mode = importContext.getImportMode();
        List<String> allColumns = importContext.getHeaderList();
        List<String> pkColumns = importContext.getPrimaryKeyColumns();

        int paramIndex = 1;
        if ("UPDATE".equals(mode)) {
            // UPDATE: SET 非主键列 WHERE 主键列
            for (String col : allColumns) {
                if (pkColumns == null || !pkColumns.contains(col)) {
                    ps.setObject(paramIndex++, normalizeValue(col, rowData.getOrDefault(col, null), importContext));
                }
            }
            if (pkColumns != null) {
                for (String pk : pkColumns) {
                    ps.setObject(paramIndex++, normalizeValue(pk, rowData.getOrDefault(pk, null), importContext));
                }
            } else {
                for (String col : allColumns) {
                    ps.setObject(paramIndex++, normalizeValue(col, rowData.getOrDefault(col, null), importContext));
                }
            }
        } else if ("DELETE".equals(mode)) {
            // DELETE: WHERE 主键列（或所有列）
            if (pkColumns != null && !pkColumns.isEmpty()) {
                for (String pk : pkColumns) {
                    ps.setObject(paramIndex++, normalizeValue(pk, rowData.getOrDefault(pk, null), importContext));
                }
            } else {
                for (String col : allColumns) {
                    ps.setObject(paramIndex++, normalizeValue(col, rowData.getOrDefault(col, null), importContext));
                }
            }
        } else {
            // INSERT / UPSERT / INSERT_IGNORE / REPLACE(已转为INSERT): 所有列
            for (String col : allColumns) {
                ps.setObject(paramIndex++, normalizeValue(col, rowData.getOrDefault(col, null), importContext));
            }
        }
    }

    private Object normalizeValue(String columnName, String rawValue, ImportContext importContext) {
        if (rawValue == null) {
            return null;
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return null;
        }

        String dataType = null;
        if (importContext.getColumnTypeMap() != null) {
            dataType = importContext.getColumnTypeMap().get(columnName);
        }
        if (dataType == null) {
            return value;
        }

        String lowerType = dataType.toLowerCase();
        if (isNumericType(lowerType)) {
            String normalizedBoolean = normalizeBooleanToNumeric(value);
            return normalizedBoolean != null ? normalizedBoolean : value;
        }

        if (isBooleanType(lowerType)) {
            String normalizedBoolean = normalizeBooleanToNumeric(value);
            if (normalizedBoolean != null) {
                return "1".equals(normalizedBoolean);
            }
        }

        return value;
    }

    private boolean isNumericType(String lowerType) {
        return Arrays.asList("tinyint", "smallint", "mediumint", "int", "integer", "bigint", "decimal", "numeric",
                "float", "double", "real", "bit").stream().anyMatch(lowerType::contains);
    }

    private boolean isBooleanType(String lowerType) {
        return lowerType.contains("boolean") || lowerType.contains("bool");
    }

    private String normalizeBooleanToNumeric(String value) {
        String lowerValue = value.toLowerCase();
        if (Arrays.asList("true", "yes", "y", "on").contains(lowerValue)) {
            return "1";
        }
        if (Arrays.asList("false", "no", "n", "off").contains(lowerValue)) {
            return "0";
        }
        return null;
    }

    private void validateMappings(List<String> fileHeaders, ImportContext importContext) {
        List<FieldMapping> mappings = importContext.getFieldMappings();
        if (mappings == null || mappings.isEmpty()) {
            Map<String, Integer> columnOrderMap = importContext.getColumnOrderMap();
            List<String> missingColumns = new ArrayList<>();
            for (String fileHeader : fileHeaders) {
                if (!columnOrderMap.containsKey(fileHeader)) {
                    missingColumns.add(fileHeader);
                }
            }
            if (!missingColumns.isEmpty()) {
                String missingColsStr = missingColumns.stream().collect(Collectors.joining(", "));
                throw new BusinessException("dataSource.importColumnNotFound",
                        new Object[]{missingColsStr});
            }
            return;
        }

        Map<String, Integer> columnOrderMap = importContext.getColumnOrderMap();
        List<String> invalidTargets = new ArrayList<>();
        for (FieldMapping mapping : mappings) {
            if (!columnOrderMap.containsKey(mapping.getTargetField())) {
                invalidTargets.add(mapping.getTargetField());
            }
        }
        if (!invalidTargets.isEmpty()) {
            String invalidStr = invalidTargets.stream().collect(Collectors.joining(", "));
            throw new BusinessException("dataSource.importInvalidTargetField",
                    new Object[]{invalidStr});
        }
    }

    private Map<String, String> convertToTargetData(Map<String, String> sourceData, ImportContext importContext) {
        Map<String, String> rowData = new LinkedHashMap<>();
        List<FieldMapping> mappings = importContext.getFieldMappings();

        if (mappings == null || mappings.isEmpty()) {
            for (String columnName : importContext.getHeaderList()) {
                rowData.put(columnName, sourceData.getOrDefault(columnName, null));
            }
            return rowData;
        }

        Map<String, String> targetToSourceMap = new LinkedHashMap<>();
        for (FieldMapping mapping : mappings) {
            targetToSourceMap.put(mapping.getTargetField(), mapping.getSourceField());
        }

        for (String targetColumn : importContext.getHeaderList()) {
            String sourceField = targetToSourceMap.get(targetColumn);
            if (sourceField != null) {
                rowData.put(targetColumn, sourceData.getOrDefault(sourceField, null));
            } else {
                rowData.put(targetColumn, null);
            }
        }

        return rowData;
    }

    public List<String> readFileHeaders(File file) {
        List<String> headers = new ArrayList<>();
        EasyExcel.read(file, new ReadListener<Map<Integer, String>>() {
            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                    headers.add(entry.getValue().getStringValue().trim());
                }
            }

            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).sheet().doRead();
        return headers;
    }

    protected PreparedStatement prepareStatement(ImportContext importContext) throws SQLException {
        SqlBuilder sqlBuilder = Chat2DBContext.getSqlBuilder();
        String mode = importContext.getImportMode();
        if ("REPLACE".equals(mode)) {
            mode = "INSERT";
        }
        String sql = sqlBuilder.buildImportSql(
                importContext.getTableName(),
                importContext.getHeaders(),
                importContext.getPrimaryKeyColumns(),
                mode
        );
        log.info("import SQL: {}", sql);
        return importContext.getConnection().prepareStatement(sql);
    }

    protected void truncateTable(ImportContext importContext) throws SQLException {
        String sql = "TRUNCATE TABLE " + importContext.getTableName();
        log.info("truncate table SQL: {}", sql);
        try (Statement stmt = importContext.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
