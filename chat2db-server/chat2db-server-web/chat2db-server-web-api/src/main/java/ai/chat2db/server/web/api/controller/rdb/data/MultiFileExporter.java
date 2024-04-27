package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface MultiFileExporter  {

       void doMultiFileExport(DatabaseExportDataParam param, HttpServletResponse response) throws IOException, SQLException;
}
