package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.model.schemaDiff.SchemaDiffResult;
import ai.chat2db.server.domain.api.param.schemaDiff.MigrateResult;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaCompareParam;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaMigrateParam;
import ai.chat2db.server.domain.api.service.SchemaDiffService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.controller.rdb.request.SchemaCompareRequest;
import ai.chat2db.server.web.api.controller.rdb.request.SchemaMigrateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rdb/schema/diff")
public class SchemaDiffController {

    @Autowired
    private SchemaDiffService schemaDiffService;

    @PostMapping("/compare")
    public DataResult<SchemaDiffResult> compare(@Valid @RequestBody SchemaCompareRequest request) {
        SchemaCompareParam param = SchemaCompareParam.builder()
                .sourceDataSourceId(request.getSourceDataSourceId())
                .sourceDatabaseName(request.getSourceDatabaseName())
                .sourceSchemaName(request.getSourceSchemaName())
                .targetDataSourceId(request.getTargetDataSourceId())
                .targetDatabaseName(request.getTargetDatabaseName())
                .targetSchemaName(request.getTargetSchemaName())
                .tableNames(request.getTableNames())
                .compareOption(request.getCompareOption() != null
                        ? request.getCompareOption()
                        : new ai.chat2db.server.domain.api.param.schemaDiff.CompareOption())
                .build();
        SchemaDiffResult result = schemaDiffService.compare(param);
        return DataResult.of(result);
    }

    @PostMapping("/migrate")
    public DataResult<MigrateResult> migrate(@Valid @RequestBody SchemaMigrateRequest request) {
        SchemaMigrateParam param = SchemaMigrateParam.builder()
                .targetDataSourceId(request.getTargetDataSourceId())
                .targetDatabaseName(request.getTargetDatabaseName())
                .targetSchemaName(request.getTargetSchemaName())
                .ddlStatements(request.getDdlStatements())
                .executeInTransaction(request.isExecuteInTransaction())
                .continueOnError(request.isContinueOnError())
                .build();
        MigrateResult result = schemaDiffService.migrate(param);
        return DataResult.of(result);
    }
}
