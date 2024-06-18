package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.BaseImportExcelDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileFactoryProducer;
import ai.chat2db.server.web.api.controller.rdb.data.sql.EasySqlBuilder;
import ai.chat2db.server.web.api.controller.rdb.data.util.EasyBatchSqlExecutor;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    private final String databaseName;
    private final String schemaName;
    private final String tableName;
    private final List<String> tableColumns;
    private final List<String> fileColumns;
    private final Connection connection;
    private final int BATCH_SIZE = 100;
    private final List<String> sqlCacheList = ListUtils.newArrayListWithExpectedSize(BATCH_SIZE);
    private final List<Integer> columnIndexList;
    private int recordIndex = 0;
    private final int limitRowSize;


    public NoModelDataListener(String databaseName, String schemaName, String tableName, List<String> tableColumns,
                               List<String> fileColumns, AbstractImportDataOptions importDataOption, Connection connection) {
        this.databaseName = databaseName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.tableColumns = tableColumns;
        this.fileColumns = fileColumns;
        this.connection = connection;
        this.columnIndexList = ListUtils.newArrayListWithExpectedSize(fileColumns.size());
        this.limitRowSize = ((BaseImportExcelDataOptions) importDataOption).getDataEndRowNum()
                - ((BaseImportExcelDataOptions) importDataOption).getDataStartRowNum() + 1;

    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        if (recordIndex++ >= limitRowSize) {
            DataFileFactoryProducer.notifyInfo(String.format("read %s records，stopping reading", limitRowSize));
            if (sqlCacheList.size() > 0) {
                EasyBatchSqlExecutor.executeBatchInsert(connection, sqlCacheList);
            }
            return false;
        }
        return super.hasNext(context);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Map<Integer, String> stringMap = ConverterUtils.convertToStringMap(headMap, context);
        for (Map.Entry<Integer, String> entry : stringMap.entrySet()) {
            String value = entry.getValue();
            if (fileColumns.contains(value)) {
                Integer key = entry.getKey();
                columnIndexList.add(key);
            }
        }
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (!columnIndexList.contains(i)) {
                continue;
            }
            String value = data.get(i);
            if (Objects.isNull(value)) {
                values.add(null);
            } else {
                values.add(value);
            }
        }
        String sql = String.format("INSERT INTO %s.%s %s VALUES %s",
                                   StringUtils.isBlank(databaseName) ? schemaName : databaseName, tableName,
                                   EasySqlBuilder.buildColumns(tableColumns),
                                   EasySqlBuilder.buildValues(values));
        values.clear();
        sqlCacheList.add(sql);
        if (sqlCacheList.size() >= BATCH_SIZE) {
            EasyBatchSqlExecutor.executeBatchInsert(connection, sqlCacheList);
        }
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (sqlCacheList.size() > 0) {
            EasyBatchSqlExecutor.executeBatchInsert(connection, sqlCacheList);
        }
    }
}

