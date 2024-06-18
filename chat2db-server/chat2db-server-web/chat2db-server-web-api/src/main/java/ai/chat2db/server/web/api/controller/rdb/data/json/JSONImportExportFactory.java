package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:22
 */
public class JSONImportExportFactory implements DataFileImportExportFactory {

    @Override
    public DataFileImporter createImporter() {
        return new JSONImporter();
    }


    @Override
    public DataFileExporter createExporter() {
        return new JSONExporter();
    }
}
