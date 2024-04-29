package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseImportDataParam;
import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.table.ImportNewTableOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.table.ImportTableOptions;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Override
    public void importDataFile(DatabaseImportDataParam param, MultipartFile file) throws IOException {
        Connection connection = Chat2DBContext.getConnection();
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        ImportTableOptions importTableOption = param.getImportTableOption();
        String tableName = importTableOption.getTableName();
        createNewTable(connection, importTableOption,tableName);
        List<String> tableColumns = importTableOption.getTableColumns();
        List<String> fileColumns = importTableOption.getFileColumns();
        doImportData(connection,databaseName, schemaName, tableName, tableColumns, fileColumns, param.getImportDataOption(), file);
        log.info("Finished successfully");
    }

    private void createNewTable(Connection connection, ImportTableOptions importTableOption, String tableName) {
        log.info("create new table:{}",tableName);
        if (importTableOption instanceof ImportNewTableOptions importNewTableOptions) {
            List<String> sqlList = importNewTableOptions.getSql();
            if (CollectionUtils.isEmpty(sqlList)) {
               return;
            }
            try (Statement statement =  connection.createStatement()){
                for (String sql : sqlList) {
                    statement.addBatch(sql);
                }
                statement.executeBatch();
                log.info("Successfully created new table");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                         List<String> tableColumns, List<String> fileColumns,
                                         AbstractImportDataOptions importDataOption, MultipartFile file) throws IOException;
}
