package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.param.MetaDataQueryParam;
import ai.chat2db.server.domain.api.param.datasource.*;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.vo.DatabaseVO;
import ai.chat2db.server.web.api.controller.rdb.converter.DatabaseConverter;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileFactoryProducer;
import ai.chat2db.server.web.api.controller.rdb.request.*;
import ai.chat2db.server.web.api.controller.rdb.vo.MetaSchemaVO;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.MetaSchema;
import ai.chat2db.spi.model.Sql;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * database controller
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/database")
@RestController
public class DatabaseController {
    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    public DatabaseConverter databaseConverter;

    /**
     * Query the database_schema_list contained in the database
     *
     * @param request
     * @return
     */
    @GetMapping("/database_schema_list")
    public DataResult<MetaSchemaVO> databaseSchemaList(@Valid DataSourceBaseRequest request) {
        MetaDataQueryParam queryParam = MetaDataQueryParam.builder().dataSourceId(request.getDataSourceId())
                .refresh(
                        request.isRefresh()).build();
        DataResult<MetaSchema> result = databaseService.queryDatabaseSchema(queryParam);
        MetaSchemaVO schemaDto2vo = rdbWebConverter.metaSchemaDto2vo(result.getData());
        return DataResult.of(schemaDto2vo);
    }

    @GetMapping("list")
    public ListResult<DatabaseVO> databaseList(@Valid DataSourceBaseRequest request) {
        DatabaseQueryAllParam queryParam = DatabaseQueryAllParam.builder().dataSourceId(request.getDataSourceId())
                .refresh(
                        request.isRefresh()).build();
        ListResult<Database> result = databaseService.queryAll(queryParam);
        return ListResult.of(rdbWebConverter.databaseDto2vo(result.getData()));
    }

    /**
     * Delete database
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_database")
    public ActionResult deleteDatabase(@Valid @RequestBody DataSourceBaseRequest request) {
        DatabaseCreateParam param = DatabaseCreateParam.builder().name(request.getDatabaseName()).build();
        return databaseService.deleteDatabase(param);
    }

    /**
     * create database
     *
     * @param request
     * @return
     */
    @PostMapping("/create_database_sql")
    public DataResult<Sql> createDatabase(@Valid @RequestBody DatabaseCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            request.setName(request.getDatabaseName());
        }
        Database database = databaseConverter.request2param(request);
        return databaseService.createDatabase(database);
    }

    /**
     * Modify database
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_database")
    public ActionResult modifyDatabase(@Valid @RequestBody UpdateDatabaseRequest request) {
        DatabaseCreateParam param = DatabaseCreateParam.builder().name(request.getDatabaseName())
                .name(request.getNewDatabaseName()).build();
        return databaseService.modifyDatabase(param);
    }

    @PostMapping("/export")
    public void exportDatabase(@Valid @RequestBody DatabaseExportRequest request, HttpServletResponse response) {
        String fileName = Objects.isNull(request.getSchemaName()) ? request.getDatabaseName() : request.getSchemaName();
        response.setContentType("text/sql");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".sql");
        response.setCharacterEncoding("utf-8");
        DatabaseExportParam param = databaseConverter.request2param(request);
        try (PrintWriter printWriter = response.getWriter()) {
            String sql = databaseService.exportDatabase(param);
            printWriter.println(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/export_data")
    public void exportData(@Valid @RequestBody DatabaseExportDataRequest request, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            DatabaseExportDataParam param = databaseConverter.request2param(request);
            DataFileExporter exporter = DataFileFactoryProducer
                    .getFactory(request.getExportDataOption().getFileType())
                    .createExporter();
            if (request.getExportTableOptions().size() > 1) {
                exporter.createMultiFileExporter().doMultiFileExport(param, response);
            } else {
                exporter.createSingleFileExporter().doSingleFileExport(param, response);
            }
        } catch (Exception e) {
            response.reset();
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/import_data")
    public ActionResult importData(@Valid @ModelAttribute(value = "file") MultipartFile file, @ModelAttribute("json") String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DatabaseImportDataRequest request = objectMapper.readValue(jsonData, DatabaseImportDataRequest.class);
            DatabaseImportDataParam param = databaseConverter.request2param(request);
            DataFileFactoryProducer.getFactory(request.getImportDataOption().getFileType())
                    .createImporter().importDataFile(param, file);
            return ActionResult.isSuccess();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
