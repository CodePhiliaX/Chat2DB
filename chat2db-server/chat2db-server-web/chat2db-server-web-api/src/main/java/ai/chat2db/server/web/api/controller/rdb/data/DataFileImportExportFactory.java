package ai.chat2db.server.web.api.controller.rdb.data;

public interface DataFileImportExportFactory {
    DataFileImporter createImporter();
    DataFileExporter createExporter();
}
