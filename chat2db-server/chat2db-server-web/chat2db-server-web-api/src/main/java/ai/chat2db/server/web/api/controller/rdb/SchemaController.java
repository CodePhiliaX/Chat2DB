package ai.chat2db.server.web.api.controller.rdb;

import java.util.List;

import ai.chat2db.server.domain.api.param.SchemaOperationParam;
import ai.chat2db.server.domain.api.param.SchemaQueryParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.SchemaCreateRequest;
import ai.chat2db.server.web.api.controller.rdb.request.UpdateSchemaRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.SchemaVO;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Sql;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * shema controller
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/schema")
@RestController
public class SchemaController {

    @Autowired
    private TableService tableService;

    @Autowired
    private DlTemplateService dlTemplateService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DatabaseService databaseService;

    /**
     * 查询数据库里包含的schema_list
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public ListResult<SchemaVO> list(@Valid DataSourceBaseRequest request) {
        SchemaQueryParam queryParam = SchemaQueryParam.builder().dataSourceId(request.getDataSourceId()).dataBaseName(
                request.getDatabaseName()).refresh(request.isRefresh()).build();
        ListResult<Schema> tableColumns = databaseService.querySchema(queryParam);
        List<SchemaVO> tableVOS = rdbWebConverter.schemaDto2vo(tableColumns.getData());
        return ListResult.of(tableVOS);
    }

    /**
     * 删除schema
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_schema")
    public ActionResult deleteSchema(@Valid @RequestBody DataSourceBaseRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName()).build();
        return databaseService.deleteSchema(param);
    }

    /**
     * 创建schema
     *
     * @param request
     * @return
     */
    @PostMapping("/create_schema_sql")
    public DataResult<Sql> createSchema(@Valid @RequestBody SchemaCreateRequest request) {
        Schema schema = Schema.builder().databaseName(request.getDatabaseName())
                .name(request.getSchemaName())
                .owner(request.getOwner())
                .comment(request.getComment())
                .build();
        return databaseService.createSchema(schema);
    }

    /**
     * 创建database
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_schema")
    public ActionResult modifySchema(@Valid @RequestBody UpdateSchemaRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName()).newSchemaName(request.getNewSchemaName()).build();
        return databaseService.modifySchema(param);
    }
}
