package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.tools.common.model.data.option.CSVImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportTableOption;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    private final String schemaName;
    private final ImportTableOption importTableOption;
    private final ImportDataOption importDataOption;
    private final Connection connection;
    private final int BATCH_SIZE = 1000;
    private final List<String> sqlCacheList = ListUtils.newArrayListWithExpectedSize(BATCH_SIZE);
    private final List<Integer> columnIndexList;
    private Integer recordIndex = 0;
    private final Integer limitRowSize ;


    public NoModelDataListener(String schemaName, ImportTableOption importTableOption, ImportDataOption importDataOption, Connection connection) {
        this.schemaName = schemaName;
        this.importTableOption = importTableOption;
        this.connection = connection;
        this.importDataOption = importDataOption;
        this.columnIndexList = ListUtils.newArrayListWithExpectedSize(importTableOption.getSrcColumnNames().size());
        this.limitRowSize= ((CSVImportDataOption) importDataOption).getDataEndRowNum()- ((CSVImportDataOption) importDataOption).getDataStartRowNum()+1;

    }


    /**
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Map<Integer, String> stringMap = ConverterUtils.convertToStringMap(headMap, context);
        for (Map.Entry<Integer, String> entry : stringMap.entrySet()) {
            List<String> srcColumnNames = importTableOption.getSrcColumnNames();
            if (srcColumnNames.contains(entry.getValue())) {
                columnIndexList.add(entry.getKey());
            }
        }
    }


    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        try {
            if (++recordIndex >= limitRowSize) {
                if (sqlCacheList.size() > 0) {
                    executeBatchInsert();
                }
                throw new ExcelAnalysisException("超出指定数据行");
            }

            log.info("解析到一条数据:{}", JSON.toJSONString(data));
            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                if (!columnIndexList.contains(i)) {
                    continue;
                }
                String value = data.get(i);
                if (StringUtils.isBlank(value)) {
                    value = "NULL";
                } else {
                    value = "'" + value + "'";
                }
                valueBuilder.append(value);
                if (i != data.size() - 1 && columnIndexList.contains(i + 1)) {
                    valueBuilder.append(",");
                }
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", importTableOption.getTableName(), getColumns(importTableOption.getTargetColumnNames()), valueBuilder);
            sqlCacheList.add(sql);

            if (sqlCacheList.size() >= 1000) {
                executeBatchInsert();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeBatchInsert() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            for (String sql : sqlCacheList) {
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
            stmt.clearBatch();
            sqlCacheList.clear();
        }
    }


    private String getColumns(List<String> columnNames) {
        return String.join(",", columnNames);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        try {
            if (sqlCacheList.size() > 0) {
                executeBatchInsert();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

