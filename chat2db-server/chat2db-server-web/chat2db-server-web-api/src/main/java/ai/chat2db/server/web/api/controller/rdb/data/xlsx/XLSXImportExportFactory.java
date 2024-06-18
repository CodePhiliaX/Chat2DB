package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:17
 */
public class XLSXImportExportFactory implements DataFileImportExportFactory {

    @Override
    public DataFileImporter createImporter() {
        return new XLSXImporter();
    }


    @Override
    public DataFileExporter createExporter() {
        return new XLSXExporter();
    }
}
