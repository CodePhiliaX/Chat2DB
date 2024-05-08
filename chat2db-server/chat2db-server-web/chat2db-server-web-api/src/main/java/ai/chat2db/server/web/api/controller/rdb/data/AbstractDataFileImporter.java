package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseImportDataParam;
import ai.chat2db.server.tools.base.enums.JdbcUrlParameterEnum;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.table.ImportNewTableOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.table.ImportTableOptions;
import ai.chat2db.server.web.api.controller.rdb.data.observer.LoggingObserver;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.util.JdbcUtils;
import ai.chat2db.spi.util.SqlUtils;
import com.alibaba.druid.DbType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 22:30
 */
@Slf4j
public abstract class AbstractDataFileImporter implements DataFileImporter {

    public static final int BATCH_SIZE = 100;
    @Override
    public void importDataFile(DatabaseImportDataParam param, MultipartFile file) throws IOException {
        configureUrlParameter();
        AbstractImportDataOptions importDataOption = param.getImportDataOption();
        Connection connection = Chat2DBContext.getConnection();
        ImportTableOptions importTableOption = param.getImportTableOption();
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        String tableName = importTableOption.getTableName();
        createNewTable(connection, importTableOption, tableName);
        List<String> tableColumns = importTableOption.getTableColumns();
        List<String> fileColumns = importTableOption.getFileColumns();
        doImportData(connection, databaseName, schemaName, tableName, tableColumns, fileColumns, importDataOption, file);
        int errorCounter = ((LoggingObserver) DataFileFactoryProducer.getObserver()).getErrorCounter();
        if (errorCounter > 0) {
            DataFileFactoryProducer.notifyInfo(String.format("Finished data file importing with errors: %s", errorCounter));
        } else {
            DataFileFactoryProducer.notifyInfo("Finished data file importing");

        }
    }


    private void configureUrlParameter() {
        KeyValue keyValue = new KeyValue();
        keyValue.setKey(JdbcUrlParameterEnum.CONTINUE_BATCH_ON_ERROR.getDescription());
        keyValue.setValue(String.valueOf(true));
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        connectInfo.setExtendInfo(List.of(keyValue));

    }

    private void createNewTable(Connection connection, ImportTableOptions importTableOption, String tableName) {
        if (importTableOption instanceof ImportNewTableOptions importNewTableOptions) {
            DataFileFactoryProducer.notifyInfo(String.format("create new table:%s", tableName));
            String createTableSql = importNewTableOptions.getSql();
            String type = Chat2DBContext.getConnectInfo().getDbType();
            DbType dbType = JdbcUtils.parse2DruidDbType(type);
            List<String> sqlList = SqlUtils.parse(createTableSql, dbType);
            if (CollectionUtils.isEmpty(sqlList)) {
                return;
            }
            try (Statement statement = connection.createStatement()) {
                for (String sql : sqlList) {
                    statement.addBatch(sql);
                }
                try {
                    statement.executeBatch();
                } catch (BatchUpdateException e) {
                    DataFileFactoryProducer.notifyError("created new table failed");
                    throw new RuntimeException(e);
                }
                DataFileFactoryProducer.notifyInfo("Successfully created new table");
            } catch (SQLException e) {
                DataFileFactoryProducer.notifyError("created new table failed");
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                         List<String> tableColumns, List<String> fileColumns,
                                         AbstractImportDataOptions importDataOption, MultipartFile file) throws IOException;
}
