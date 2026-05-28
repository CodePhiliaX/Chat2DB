package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SqlImportStrategy implements ImportStrategy {

    @Override
    public void importData(File file, ImportContext importContext) {
        final AtomicInteger processedCount = new AtomicInteger(0);

        try (Statement statement = importContext.getConnection().createStatement()) {
            List<String> sqlStatements = readSqlStatements(file);

            for (String sql : sqlStatements) {
                String trimmedSql = sql.trim();
                if (StringUtils.isNotBlank(trimmedSql) && !trimmedSql.startsWith("--")) {
                    statement.executeUpdate(trimmedSql);
                    int count = processedCount.incrementAndGet();

                    if (count % 200 == 0) {
                        importContext.getProgressUpdater().accept(count);
                    }
                }
            }
            importContext.getProgressUpdater().accept(processedCount.get());
        } catch (SQLException | IOException e) {
            log.error("import sql error", e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()});
        }
    }

    /**
     * Read SQL statements from file, handling multi-line statements correctly.
     * Splits by semicolon (;) while preserving multi-line content.
     */
    private List<String> readSqlStatements(File file) throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentStatement.append(line).append("\n");

                // Check if line contains semicolon (end of statement)
                if (line.trim().endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (StringUtils.isNotBlank(sql)) {
                        statements.add(sql);
                    }
                    currentStatement = new StringBuilder();
                }
            }

            // Handle remaining content (statement without trailing semicolon)
            String remaining = currentStatement.toString().trim();
            if (StringUtils.isNotBlank(remaining)) {
                statements.add(remaining);
            }
        }

        return statements;
    }

    @Override
    public boolean supports(String fileType) {
        return false;
    }
}
