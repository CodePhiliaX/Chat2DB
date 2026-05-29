package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.domain.api.enums.TaskStatusEnum;
import ai.chat2db.server.domain.api.enums.TaskTypeEnum;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.task.request.DataImportRequest;
import ai.chat2db.server.web.api.controller.task.request.FieldMapping;
import ai.chat2db.server.web.api.controller.task.request.FilePreviewRequest;
import ai.chat2db.server.web.api.controller.task.response.FilePreviewResult;
import ai.chat2db.server.web.api.controller.task.response.TableColumnInfo;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.model.Header;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImportBizService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TaskService taskService;

    @Autowired
    private TableService tableService;

    @Autowired
    private ImportStrategyFactory strategyFactory;

    public DataResult<Long> importData(MultipartFile file, DataImportRequest request) {
        Assert.notNull(file, "file can not be null");
        Assert.notBlank(request.getTableName(), "tableName can not be blank");
        if ("SQL".equalsIgnoreCase(request.getFileType())) {
            throw new BusinessException("dataSource.importSqlNotSupported");
        }

        DataResult<Long> dataResult = createImportTask(request);

        LoginUser loginUser = ContextUtils.getLoginUser();
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo().copy();

        // 同步保存文件到安全位置，避免异步执行时临时文件被清理
        File safeFile;
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "import_file";
            }
            safeFile = FileUtil.createTempFile(originalFilename, "", true);
            file.transferTo(safeFile);
        } catch (Exception e) {
            log.error("save upload file error", e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }

        final File finalSafeFile = safeFile;

        CompletableFuture.runAsync(() -> {
            buildContext(loginUser, connectInfo);
            try {
                doImportData(finalSafeFile, request, dataResult.getData());
            } finally {
                // 确保清理临时文件
                FileUtil.del(finalSafeFile);
            }
        }).whenComplete((aVoid, throwable) -> {
            updateImportStatus(dataResult.getData(), throwable);
            removeContext();
        });

        return dataResult;
    }

    /**
     * 预览文件表头和目标表字段
     */
    public DataResult<FilePreviewResult> previewHeaders(MultipartFile file, FilePreviewRequest request) {
        Assert.notNull(file, "file can not be null");
        Assert.notBlank(request.getTableName(), "tableName can not be blank");

        // 保存临时文件
        File tempFile;
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "preview_file";
            }
            tempFile = FileUtil.createTempFile(originalFilename, "", true);
            file.transferTo(tempFile);
        } catch (Exception e) {
            log.error("save upload file error", e);
            throw new BusinessException("dataSource.importError", new Object[]{e.getMessage()}, e);
        }

        try {
            FilePreviewResult result = new FilePreviewResult();

            // 获取文件表头
            String fileType = request.getFileType().toUpperCase();
            ImportStrategy strategy = strategyFactory.getStrategy(fileType);
            if (strategy instanceof AbstractImportStrategy abstractStrategy) {
                List<String> fileHeaders = abstractStrategy.readFileHeaders(tempFile);
                result.setFileHeaders(fileHeaders);
            } else {
                throw new BusinessException("dataSource.unsupportedFileType", new Object[]{fileType});
            }

            // 获取目标表字段
            List<TableColumn> columns = getColumnList(request.getDataSourceId(), request.getDatabaseName(),
                    request.getSchemaName(), request.getTableName());
            List<TableColumnInfo> tableColumns = columns.stream().map(col -> {
                TableColumnInfo info = new TableColumnInfo();
                info.setName(col.getName());
                info.setType(col.getColumnType());
                info.setPrimaryKey(Boolean.TRUE.equals(col.getPrimaryKey()));
                return info;
            }).collect(Collectors.toList());
            result.setTableColumns(tableColumns);

            // 自动匹配同名字段
            List<String> fileHeaders = result.getFileHeaders();
            Map<String, TableColumn> columnMap = columns.stream()
                    .collect(Collectors.toMap(TableColumn::getName, col -> col));
            List<FilePreviewResult.AutoMapping> autoMappings = new ArrayList<>();
            for (String fileHeader : fileHeaders) {
                FilePreviewResult.AutoMapping mapping = new FilePreviewResult.AutoMapping();
                mapping.setSourceField(fileHeader);
                if (columnMap.containsKey(fileHeader)) {
                    mapping.setTargetField(fileHeader);
                    mapping.setMatched(true);
                } else {
                    mapping.setTargetField("");
                    mapping.setMatched(false);
                }
                autoMappings.add(mapping);
            }
            result.setAutoMappings(autoMappings);

            return DataResult.of(result);
        } finally {
            FileUtil.del(tempFile);
        }
    }

    private DataResult<Long> createImportTask(DataImportRequest request) {
        TaskCreateParam param = new TaskCreateParam();
        param.setTaskName("import_" + request.getTableName());
        param.setTaskType(TaskTypeEnum.UPLOAD_TABLE_DATA.name());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        param.setTableName(request.getTableName());
        param.setDataSourceId(request.getDataSourceId());
        param.setUserId(ContextUtils.getUserId());
        param.setTaskProgress("0");
        Long taskId = taskService.create(param);
        return DataResult.of(taskId);
    }

    private void updateImportStatus(Long id, Throwable throwable) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(id);
        if (throwable != null) {
            log.error("import error", throwable);
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

    private void doImportData(File file, DataImportRequest request, Long taskId) {
        String fileType = request.getFileType().toUpperCase();

        List<TableColumn> columns = getColumnList(request.getDataSourceId(), request.getDatabaseName(),
                request.getSchemaName(), request.getTableName());
        List<String> headerList = columns.stream().map(TableColumn::getName).collect(Collectors.toList());

        // 解析字段映射配置
        List<FieldMapping> fieldMappings = parseFieldMappings(request.getFieldMappings());

        // 提取主键列
        List<String> primaryKeyColumns = columns.stream()
                .filter(col -> Boolean.TRUE.equals(col.getPrimaryKey()))
                .map(TableColumn::getName)
                .collect(Collectors.toList());

        // 构建Header列表用于SqlBuilder
        List<Header> headers = columns.stream().map(col -> Header.builder()
                .name(col.getName())
                .dataType(col.getColumnType())
                .primaryKey(col.getPrimaryKey())
                .build()).collect(Collectors.toList());

        // 设置默认导入模式
        String importMode = request.getImportMode();
        if (importMode == null || importMode.isBlank()) {
            importMode = "INSERT";
        }

        ImportStrategy strategy = strategyFactory.getStrategy(fileType);

        Connection connection = Chat2DBContext.getConnection();

        Map<String, Integer> columnOrderMap = new HashMap<>();
        Map<String, String> columnTypeMap = new HashMap<>();
        for (int i = 0; i < headerList.size(); i++) {
            columnOrderMap.put(headerList.get(i), i);
        }
        for (TableColumn column : columns) {
            columnTypeMap.put(column.getName(), column.getColumnType());
        }

        ImportContext importContext = ImportContext.builder()
                .taskId(taskId)
                .tableName(request.getTableName())
                .headerList(headerList)
                .columnOrderMap(columnOrderMap)
                .columnTypeMap(columnTypeMap)
                .columnCount(headerList.size())
                .connection(connection)
                .progressUpdater(count -> updateProgressCount(taskId, count))
                .fieldMappings(fieldMappings)
                .importMode(importMode)
                .primaryKeyColumns(primaryKeyColumns)
                .headers(headers)
                .build();
        strategy.importData(file, importContext);
    }

    /**
     * 解析字段映射配置
     */
    private List<FieldMapping> parseFieldMappings(String fieldMappingsJson) {
        if (fieldMappingsJson == null || fieldMappingsJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(fieldMappingsJson, new TypeReference<List<FieldMapping>>() {});
        } catch (Exception e) {
            log.error("parse field mappings error", e);
            throw new BusinessException("dataSource.invalidFieldMapping", new Object[]{e.getMessage()});
        }
    }

    private List<TableColumn> getColumnList(Long dataSourceId, String databaseName, String schemaName, String tableName) {
        TableQueryParam queryParam = new TableQueryParam();
        queryParam.setDataSourceId(dataSourceId);
        queryParam.setDatabaseName(databaseName);
        queryParam.setSchemaName(schemaName);
        queryParam.setTableName(tableName);
        return tableService.queryColumns(queryParam);
    }

    private void updateProgressCount(Long taskId, int processedCount) {
        TaskUpdateParam updateParam = new TaskUpdateParam();
        updateParam.setId(taskId);
        updateParam.setTaskProgress(String.valueOf(processedCount));
        taskService.updateStatus(updateParam);
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

}
