package ai.chat2db.server.web.api.controller.rdb.data.xls;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;

/**
 * @author: zgq
 * @date: 2024年04月29日 21:48
 */
public class XLSImportExportFactory implements DataFileImportExportFactory {


    @Override
    public DataFileImporter createImporter() {
        return new XLSImporter();
    }


    @Override
    public DataFileExporter createExporter() {
        return new XLSExporter();
    }
}
