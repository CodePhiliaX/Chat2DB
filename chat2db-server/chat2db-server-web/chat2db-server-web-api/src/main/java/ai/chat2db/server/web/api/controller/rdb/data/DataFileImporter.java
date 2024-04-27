package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseImportDataParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DataFileImporter {

    void importDataFile(DatabaseImportDataParam param, MultipartFile file) throws IOException;
}
