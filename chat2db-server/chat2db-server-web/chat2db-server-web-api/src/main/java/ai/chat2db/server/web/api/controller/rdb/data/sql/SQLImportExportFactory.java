package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 15:32
 */
public class SQLImportExportFactory implements DataFileImportExportFactory {
    /**
     * @return
     */
    @Override
    public DataFileImporter createImporter() {
        return new SQLImporter();
    }

    /**
     * @return
     */
    @Override
    public DataFileExporter createExporter() {
        return new SQLExporter();
    }
}
