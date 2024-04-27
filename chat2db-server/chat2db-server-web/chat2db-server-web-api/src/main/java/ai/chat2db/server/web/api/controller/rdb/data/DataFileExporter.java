package ai.chat2db.server.web.api.controller.rdb.data;

/**
 * @author: zgq
 * @date: 2024年04月26日 10:44
 */
public interface DataFileExporter {


    SingleFileExporter createSingleFileExporter();

    MultiFileExporter createMultiFileExporter();

}
