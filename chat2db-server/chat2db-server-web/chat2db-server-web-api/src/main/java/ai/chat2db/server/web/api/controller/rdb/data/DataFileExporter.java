package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author: zgq
 * @date: 2024年04月26日 10:44
 */
public interface DataFileExporter {

    void exportDataFile(DatabaseExportDataParam param, HttpServletResponse response) throws IOException, SQLException;

}
