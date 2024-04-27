package ai.chat2db.server.web.api.controller.rdb.data.json;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 14:20
 */
public class JSONExporter implements DataFileExporter {

    @Override
    public SingleFileExporter createSingleFileExporter() {
        return new SingleJSONExporter();
    }


    @Override
    public MultiFileExporter createMultiFileExporter() {
        return new MultiJSONExporter();
    }
}
