package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:05
 */
public class XLSXExporter implements DataFileExporter {


    @Override
    public SingleFileExporter createSingleFileExporter() {
        return new SingleXLSXExporter();
    }


    @Override
    public MultiFileExporter createMultiFileExporter() {
        return new MultiXLSXExporter();
    }
}
