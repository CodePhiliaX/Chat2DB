package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:50
 */
public class ExportDBData2SqlStrategy extends ExportDBDataStrategy {

    public ExportDBData2SqlStrategy() {
        suffix = ExportFileSuffix.SQL.getSuffix();
        contentType = "text/sql";
    }

    @Override
    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {
        String databaseName = param.getDatabaseName();
        String schemaName = param.getSchemaName();
        String fileName = Objects.isNull(schemaName) ? databaseName : schemaName;
        response.setContentType(getContentType());
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + getSuffix());
        try (PrintWriter writer = response.getWriter()) {
            writer.println(Chat2DBContext.getDBManage().exportDatabaseData(Chat2DBContext.getConnection(), databaseName, schemaName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
