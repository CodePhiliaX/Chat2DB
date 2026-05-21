package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.param.datasource.DatabaseCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DatabaseQueryAllParam;
import ai.chat2db.server.domain.api.param.MetaDataQueryParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.vo.DatabaseVO;
import ai.chat2db.server.web.api.controller.rdb.converter.DatabaseConverter;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DatabaseCreateRequest;
import ai.chat2db.server.web.api.controller.rdb.request.UpdateDatabaseRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.MetaSchemaVO;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.MetaSchema;
import ai.chat2db.spi.model.Sql;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * 查询数据库和 Schema 列表
     * <p>
     * 返回当前数据源下所有的 Database 和 Schema 信息
     * 适用于 PostgreSQL 等支持 Schema 概念的数据库，可以同时获取 database 和 schema 列表
     * </p>
     *
     * @param request 请求参数，包含数据源 ID
     * @return MetaSchemaVO，包含 databases（数据库列表）和 schemas（Schema 列表）
     */
    @GetMapping("/database_schema_list")
    public DataResult<MetaSchemaVO> databaseSchemaList(@Valid DataSourceBaseRequest request) {
        MetaDataQueryParam queryParam = MetaDataQueryParam.builder().dataSourceId(request.getDataSourceId())
            .refresh(
            request.isRefresh()).build();
        MetaSchema result = databaseService.queryDatabaseSchema(queryParam);
        MetaSchemaVO schemaDto2vo = rdbWebConverter.metaSchemaDto2vo(result);
        return DataResult.of(schemaDto2vo);
    }

    /**
     * 查询数据库列表
     * <p>
     * 返回当前数据源下所有的 Database 信息
     * 仅获取数据库列表，不包含 Schema 信息
     * </p>
     *
     * @param request 请求参数，包含数据源 ID
     * @return DatabaseVO 列表，每个 DatabaseVO 包含数据库名称、关联的 schemas、注释等信息
     */
    @GetMapping("list")
    public ListResult<DatabaseVO> databaseList(@Valid DataSourceBaseRequest request) {
        DatabaseQueryAllParam queryParam = DatabaseQueryAllParam.builder().dataSourceId(request.getDataSourceId())
            .refresh(
                request.isRefresh()).build();
        List<Database> result = databaseService.queryAll(queryParam);
        return ListResult.of(rdbWebConverter.databaseDto2vo(result));
    }

    /**
     * 删除数据库
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_database")
    public ActionResult deleteDatabase(@Valid @RequestBody DataSourceBaseRequest request) {
        DatabaseCreateParam param = DatabaseCreateParam.builder().name(request.getDatabaseName()).build();
        databaseService.deleteDatabase(param);
        return ActionResult.isSuccess();
    }

    /**
     * 创建database
     *
     * @param request
     * @return
     */
    @PostMapping("/create_database_sql")
    public DataResult<Sql> createDatabase(@Valid @RequestBody DatabaseCreateRequest request) {
        if(StringUtils.isBlank(request.getName())){
            request.setName(request.getDatabaseName());
        }
        Database database = databaseConverter.request2param(request);
        return DataResult.of(databaseService.createDatabase(database));
    }

    /**
     * 修改database
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_database")
    public ActionResult modifyDatabase(@Valid @RequestBody UpdateDatabaseRequest request) {
        DatabaseCreateParam param = DatabaseCreateParam.builder().name(request.getDatabaseName())
            .name(request.getNewDatabaseName()).build();
        databaseService.modifyDatabase(param);
        return ActionResult.isSuccess();
    }
}
