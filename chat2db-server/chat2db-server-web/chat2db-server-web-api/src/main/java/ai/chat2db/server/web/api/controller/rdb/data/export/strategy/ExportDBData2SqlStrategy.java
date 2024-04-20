package ai.chat2db.server.web.api.controller.rdb.data.export.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        contentType = "text/sql";
    }

    @Override
    protected ByteArrayOutputStream exportData(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteOut, StandardCharsets.UTF_8))) {
            writer.println(export2SQL(connection, databaseName, schemaName, tableName));
        }
        return byteOut;
    }
    @Override
    protected void exportData(HttpServletResponse response, Connection connection,
                              String databaseName, String schemaName, String tableName) throws SQLException {
        try {
            response.getWriter().print(export2SQL(connection, databaseName, schemaName, tableName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String export2SQL(Connection connection, String databaseName, String schemaName, String tableName) throws SQLException {
        return Chat2DBContext.getDBManage().exportDatabaseData(connection, databaseName, schemaName, tableName);
    }
}