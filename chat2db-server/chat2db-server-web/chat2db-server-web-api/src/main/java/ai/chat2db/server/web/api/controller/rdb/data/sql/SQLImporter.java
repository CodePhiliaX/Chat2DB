package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.tools.common.model.data.option.AbstractImportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.BaseFileImporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.util.List;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 15:33
 */
public class SQLImporter extends BaseFileImporter implements DataFileImporter {

    @Override
    protected void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                List<String> tableColumns, List<String> fileColumns,
                                AbstractImportDataOptions importDataOption, MultipartFile file) {

    }
}
