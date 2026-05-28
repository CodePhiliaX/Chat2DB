package ai.chat2db.server.web.api.controller.sql.biz;

import ai.chat2db.server.domain.api.enums.TaskStatusEnum;
import ai.chat2db.server.domain.api.enums.TaskTypeEnum;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.sql.request.SqlFileExecuteRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SqlExecuteBizService {

    @Autowired
    private TaskService taskService;

    public DataResult<Long> executeSqlFile(MultipartFile file, SqlFileExecuteRequest request) {
        if (file == null) {
            throw new BusinessException("common.paramError");
        }

        DataResult<Long> dataResult = createExecuteTask(request);
        LoginUser loginUser = ContextUtils.getLoginUser();
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo().copy();

        File safeFile;
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "sql_execute_file";
            }
            safeFile = FileUtil.createTempFile(originalFilename, "", true);
            file.transferTo(safeFile);
        } catch (Exception e) {
            log.error("save sql file error", e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }

        final File finalSafeFile = safeFile;
        CompletableFuture.runAsync(() -> {
            buildContext(loginUser, connectInfo);
            try {
                doExecuteSql(finalSafeFile, dataResult.getData());
            } finally {
                FileUtil.del(finalSafeFile);
            }
        }).whenComplete((aVoid, throwable) -> {
            updateTaskStatus(dataResult.getData(), throwable);
            removeContext();
        });

        return dataResult;
    }

    private DataResult<Long> createExecuteTask(SqlFileExecuteRequest request) {
        TaskCreateParam param = new TaskCreateParam();
        param.setTaskName("execute_sql_file");
        param.setTaskType(TaskTypeEnum.UPLOAD_TABLE_STRUCTURE.name());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        param.setDataSourceId(request.getDataSourceId());
        param.setUserId(ContextUtils.getUserId());
        param.setTaskProgress("0");
        Long taskId = taskService.create(param);
        return DataResult.of(taskId);
    }

    private void doExecuteSql(File file, Long taskId) {
        final AtomicInteger processedCount = new AtomicInteger(0);
        Connection connection = Chat2DBContext.getConnection();
        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }

        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            List<SqlStatementItem> sqlStatements = readSqlStatements(file);
            for (SqlStatementItem item : sqlStatements) {
                String trimmedSql = item.getSql().trim();
                if (StringUtils.isNotBlank(trimmedSql) && !trimmedSql.startsWith("--")) {
                    statement.execute(trimmedSql);
                    int count = processedCount.incrementAndGet();
                    if (count % 200 == 0) {
                        updateProgressCount(taskId, count);
                    }
                }
            }
            connection.commit();
            updateProgressCount(taskId, processedCount.get());
        } catch (Exception e) {
            rollbackQuietly(connection);
            if (e instanceof SQLException sqlException) {
                int failedIndex = processedCount.get() + 1;
                throw new BusinessException(
                        "dataSource.executeSqlErrorDetail",
                        new Object[]{
                                failedIndex,
                                findStatementLine(file, failedIndex),
                                sqlException.getMessage(),
                                buildSqlSnippet(file, failedIndex)
                        },
                        e
                );
            }
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                log.warn("restore autoCommit failed", e);
            }
        }
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            log.warn("rollback failed", ex);
        }
    }

    private int findStatementLine(File file, int statementIndex) {
        try {
            List<SqlStatementItem> statements = readSqlStatements(file);
            if (statementIndex <= 0 || statementIndex > statements.size()) {
                return -1;
            }
            return statements.get(statementIndex - 1).getStartLine();
        } catch (IOException ignore) {
            return -1;
        }
    }

    private String buildSqlSnippet(File file, int statementIndex) {
        try {
            List<SqlStatementItem> statements = readSqlStatements(file);
            if (statementIndex <= 0 || statementIndex > statements.size()) {
                return "";
            }
            String sql = statements.get(statementIndex - 1).getSql();
            sql = sql.replaceAll("\\s+", " ").trim();
            if (sql.length() > 300) {
                return sql.substring(0, 300) + "...";
            }
            return sql;
        } catch (IOException ignore) {
            return "";
        } catch (Exception e) {
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }
    }

    private List<SqlStatementItem> readSqlStatements(File file) throws IOException {
        List<SqlStatementItem> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        int startLine = 1;
        int lineNo = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (currentStatement.length() == 0) {
                    startLine = lineNo;
                }
                currentStatement.append(line).append("\n");
                if (line.trim().endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (StringUtils.isNotBlank(sql)) {
                        statements.add(new SqlStatementItem(sql, startLine));
                    }
                    currentStatement = new StringBuilder();
                }
            }
            String remaining = currentStatement.toString().trim();
            if (StringUtils.isNotBlank(remaining)) {
                statements.add(new SqlStatementItem(remaining, startLine));
            }
        }
        return statements;
    }

    private static class SqlStatementItem {
        private final String sql;
        private final int startLine;

        private SqlStatementItem(String sql, int startLine) {
            this.sql = sql;
            this.startLine = startLine;
        }

        public String getSql() {
            return sql;
        }

        public int getStartLine() {
            return startLine;
        }
    }

    private void updateProgressCount(Long taskId, int processedCount) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(taskId);
        updateParam.setTaskProgress(String.valueOf(processedCount));
        taskService.updateStatus(updateParam);
    }

    private void updateTaskStatus(Long id, Throwable throwable) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(id);
        if (throwable != null) {
            log.error("execute sql file error", throwable);
            updateParam.setTaskStatus(TaskStatusEnum.ERROR.name());
            if (throwable.getCause() instanceof BusinessException businessException) {
                updateParam.setContent(I18nUtils.getMessage(businessException.getCode(), businessException.getArgs()));
            } else {
                updateParam.setContent(throwable.getMessage());
            }
        } else {
            updateParam.setTaskStatus(TaskStatusEnum.FINISH.name());
        }
        taskService.updateStatus(updateParam);
    }

    private void removeContext() {
        Dbutils.removeSession();
        ContextUtils.removeContext();
        Chat2DBContext.removeContext();
    }

    private void buildContext(LoginUser loginUser, ConnectInfo connectInfo) {
        ContextUtils.setContext(Context.builder().loginUser(loginUser).build());
        Dbutils.setSession();
        Chat2DBContext.putContext(connectInfo);
    }
}
