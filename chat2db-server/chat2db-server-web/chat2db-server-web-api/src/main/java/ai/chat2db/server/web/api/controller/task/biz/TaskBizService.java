package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.domain.api.enums.*;
import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.server.web.api.controller.task.biz.doc.SchemaDocExportContext;
import ai.chat2db.server.web.api.controller.task.biz.doc.SchemaDocExportStrategy;
import ai.chat2db.server.web.api.controller.task.biz.doc.SchemaDocExportStrategyFactory;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.util.JdbcUtils;
import ai.chat2db.spi.util.SqlUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TaskBizService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TableService tableService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private ExportStrategyFactory exportStrategyFactory;

    @Autowired
    private SchemaDocExportStrategyFactory schemaDocExportStrategyFactory;

    public DataResult<Long> exportResultData(DataExportRequest request) {
        String sql = ExportSizeEnum.CURRENT_PAGE.getCode().equals(request.getExportSize()) ? request.getSql() : request.getOriginalSql();
        Assert.notBlank(sql, "dataSource.sqlEmpty");
        DbType dbType = JdbcUtils.parse2DruidDbType(Chat2DBContext.getConnectInfo().getDbType());
        String tableName = getTableName(request, sql, dbType);
        File file = createTempFile(tableName, request.getExportType());

        DataResult<Long> dataResult = createTask(tableName, request.getDatabaseName(), request.getSchemaName(), request.getDataSourceId(), tableName);

        LoginUser loginUser = ContextUtils.getLoginUser();
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo().copy();

        CompletableFuture.runAsync(() -> {
            buildContext(loginUser, connectInfo);
            doExportStreaming(sql, file, dbType, tableName, request.getExportType(), dataResult.getData());
        }).whenComplete((aVoid, throwable) -> {
            updateStatus(dataResult.getData(), file, throwable);
            removeContext();
        });
        return dataResult;
    }

    public DataResult<Long> exportSchemaDoc(DataExportRequest request) {
        File file = createTempFile(request.getDatabaseName(), request.getExportType());
        DataResult<Long> dataResult = createTask(null, request.getDatabaseName(), request.getSchemaName(), request.getDataSourceId(), "schema_doc");
        LoginUser loginUser = ContextUtils.getLoginUser();
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo().copy();
        CompletableFuture.runAsync(() -> {
            buildContext(loginUser, connectInfo);
            doExportDoc(request, file);
        }).whenComplete((aVoid, throwable) -> {
            updateStatus(dataResult.getData(), file, throwable);
            removeContext();
        });
        return dataResult;
    }

    private void doExportDoc(DataExportRequest request, File file) {
        try {
            TablePageQueryParam queryParam = rdbWebConverter.tablePageRequest2param(request);
            TableSelector tableSelector = new TableSelector();
            tableSelector.setColumnList(true);
            tableSelector.setIndexList(true);
            ServicePage<Table> tablePage = tableService.pageQuery(queryParam, tableSelector);
            List<TableVO> tableVOS = rdbWebConverter.tableDto2vo(tablePage.getData());

            SchemaDocExportContext context = SchemaDocExportContext.builder()
                    .tables(tableVOS)
                    .databaseName(request.getDatabaseName())
                    .file(file)
                    .exportOptions(new ExportOptions())
                    .build();

            SchemaDocExportStrategy strategy = schemaDocExportStrategyFactory.getStrategy(request.getExportType());
            strategy.export(context);
        } catch (Exception e) {
            log.error("export schema doc error", e);
            throw new BusinessException("dataSource.exportError");
        }
    }

    private void removeContext() {
        Dbutils.removeSession();
        ContextUtils.removeContext();
        Chat2DBContext.removeContext();
    }

    private void buildContext(LoginUser loginUser, ConnectInfo connectInfo) {
        ContextUtils.setContext(Context.builder()
                .loginUser(loginUser)
                .build());
        Dbutils.setSession();
        Chat2DBContext.putContext(connectInfo);
    }

    private DataResult<Long> createTask(String tableName, String databaseName, String schemaName, Long datasourceId, String taskName) {
        TaskCreateParam param = new TaskCreateParam();
        param.setTaskName("export_" + taskName);
        param.setTaskType(TaskTypeEnum.DOWNLOAD_TABLE_DATA.name());
        param.setDatabaseName(databaseName);
        param.setSchemaName(schemaName);
        param.setTableName(tableName);
        param.setDataSourceId(datasourceId);
        param.setUserId(ContextUtils.getUserId());
        param.setTaskProgress("0");
        return taskService.create(param);
    }

    private void updateStatus(Long id, File file, Throwable throwable) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(id);
        updateParam.setDownloadUrl(file.getAbsolutePath());
        if (throwable != null) {
            log.error("export error", throwable);
            updateParam.setTaskStatus(TaskStatusEnum.ERROR.name());
        } else {
            updateParam.setTaskStatus(TaskStatusEnum.FINISH.name());
        }
        taskService.updateStatus(updateParam);
    }

    private void doExportStreaming(String sql, File file, DbType dbType, String tableName, String exportType, Long taskId) {
        ExportStrategy strategy = exportStrategyFactory.getStrategy(exportType);

        ExportContext exportContext = ExportContext.builder()
                .sql(sql)
                .file(file)
                .dbType(dbType)
                .tableName(tableName)
                .taskId(taskId)
                .progressUpdater(count -> updateProgressCount(taskId, count))
                .build();

        strategy.exportData(exportContext);
    }

    private void updateProgressCount(Long taskId, int processedCount) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(taskId);
        updateParam.setTaskProgress(String.valueOf(processedCount));
        taskService.updateStatus(updateParam);
    }

    private File createTempFile(String tableName, String exportType) {
        String fileName = URLEncoder.encode(
                        tableName + "_" + LocalDateTime.now().format(DatePattern.PURE_DATETIME_FORMATTER),
                        StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        if (ExportTypeEnum.CSV.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ".csv", true);
        } else if (ExportTypeEnum.INSERT.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ".sql", true);
        } else if (ExportTypeEnum.EXCEL.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.EXCEL.getSuffix(), true);
        } else if (ExportTypeEnum.MARKDOWN.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.MARKDOWN.getSuffix(), true);
        } else if (ExportTypeEnum.WORD.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.WORD.getSuffix(), true);
        } else if (ExportTypeEnum.PDF.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.PDF.getSuffix(), true);
        } else if (ExportTypeEnum.HTML.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.HTML.getSuffix(), true);
        } else if (ExportTypeEnum.SQL.getCode().equals(exportType)) {
            return FileUtil.createTempFile(fileName, ExportFileSuffix.SQL.getSuffix(), true);
        }
        return FileUtil.createTempFile(fileName, ".txt", true);
    }

    private String getTableName(DataExportRequest request, String sql, DbType dbType) {
        String tableName = null;
        if (dbType != null) {
            SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
            if (!(sqlStatement instanceof SQLSelectStatement)) {
                throw new BusinessException("dataSource.sqlAnalysisError");
            }
            tableName = SqlUtils.getTableName(sql, dbType);
        } else {
            tableName = StringUtils.join(Lists.newArrayList(request.getDatabaseName(), request.getSchemaName()), "_");
        }
        return tableName;
    }
}
