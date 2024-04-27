package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:21
 */
public class CSVImportExportFactory implements DataFileImportExportFactory {
    /**
     * @return
     */
    @Override
    public DataFileImporter createImporter() {
        return new CSVImporter();
    }

    /**
     * @return
     */
    @Override
    public DataFileExporter createExporter() {
       return new CSVExporter();
    }
}
