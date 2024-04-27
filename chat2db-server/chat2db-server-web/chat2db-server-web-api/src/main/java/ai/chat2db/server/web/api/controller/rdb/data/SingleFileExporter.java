package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;

public interface SingleFileExporter  {

    void doSingleFileExport(DatabaseExportDataParam param, HttpServletResponse response) throws SQLException;
}
