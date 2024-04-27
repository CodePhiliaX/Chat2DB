package ai.chat2db.server.web.api.controller.rdb.data.sql;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.MultiFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;

/**
 * @author: zgq
 * @date: 2024年04月26日 15:33
 */
public class SQLExporter implements DataFileExporter {
    /**
     * @return
     */
    @Override
    public SingleFileExporter createSingleFileExporter() {
        return new SingleSQLExporter();
    }

    /**
     * @return
     */
    @Override
    public MultiFileExporter createMultiFileExporter() {
        return new MultiSQLExporter();
    }
}
