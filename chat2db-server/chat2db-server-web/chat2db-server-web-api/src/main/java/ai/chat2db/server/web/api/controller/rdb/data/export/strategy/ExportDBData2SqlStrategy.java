package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.spi.sql.Chat2DBContext;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:50
 */
public class ExportDBData2SqlStrategy extends ExportDBDataStrategy {

    public ExportDBData2SqlStrategy() {
        suffix = ExportFileSuffix.SQL.getSuffix();
        contentType = "application/zip";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            String sql = Chat2DBContext.getDBManage().exportDatabaseData(connection, databaseName, schemaName, tableName);
            writer.println(sql);
        }
        return byteOut;
    }
}