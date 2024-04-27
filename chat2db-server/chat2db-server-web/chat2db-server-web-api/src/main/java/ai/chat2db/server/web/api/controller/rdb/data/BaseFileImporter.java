package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseImportDataParam;
import ai.chat2db.server.tools.common.model.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.table.ImportTableOptions;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 22:30
 */
public abstract class BaseFileImporter implements DataFileImporter {

    @Override
    public void importDataFile(DatabaseImportDataParam param, MultipartFile file) throws IOException {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        ImportTableOptions importTableOption = param.getImportTableOption();
        String tableName = importTableOption.getTableName();
        List<String> tableColumns = importTableOption.getTableColumns();
        List<String> fileColumns = importTableOption.getFileColumns();
        doImportData(Chat2DBContext.getConnection(),databaseName, schemaName, tableName, tableColumns, fileColumns, param.getImportDataOption(), file);
    }

    protected abstract void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                         List<String> tableColumns, List<String> fileColumns,
                                         AbstractImportDataOptions importDataOption, MultipartFile file) throws IOException;
}
