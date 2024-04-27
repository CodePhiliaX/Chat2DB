package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.table.BaseTableOptions;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 11:39
 */
public abstract class AbstractSingleFileExporter implements SingleFileExporter {

    public String suffix;
    public String contentType;


    @Override
    public void doSingleFileExport(DatabaseExportDataParam param, HttpServletResponse response) throws SQLException {
        BaseTableOptions tableOptions = param.getExportTableOptions().get(0);
        String tableName = tableOptions.getTableName();
        List<String> tableColumns = tableOptions.getTableColumns();
        String schemaName = param.getSchemaName();
        setResponseHeaders(tableName, response);
        doTableDataExport(response, Chat2DBContext.getConnection(), param.getDatabaseName(),
                          schemaName, tableName, tableColumns, param.getExportDataOption());
    }

    protected abstract void doTableDataExport(HttpServletResponse response, Connection connection,
                                              String databaseName, String schemaName, String tableName,
                                              List<String> tableColumns, AbstractExportDataOptions exportDataOption) throws SQLException;

    private void setResponseHeaders(String tableName, HttpServletResponse response) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=" + tableName + suffix);
    }


}
