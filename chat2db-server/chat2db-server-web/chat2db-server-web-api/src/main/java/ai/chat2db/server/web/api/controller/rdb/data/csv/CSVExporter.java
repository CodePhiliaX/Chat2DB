package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:27
 */
public class CSVExporter   implements DataFileExporter {


    /**
     * @return
     */
    @Override
    public SingleFileExporter createSingleFileExporter() {
        return new SingleCSVExporter();
    }

    /**
     * @return
     */
    @Override
    public MultiFileExporter createMultiFileExporter() {
        return new MultiCSVExporter();
    }
}
