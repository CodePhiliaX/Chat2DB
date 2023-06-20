package com.alibaba.dbhub.server.web.api.controller.rdb;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.param.DatabaseOperationParam;
import com.alibaba.dbhub.server.domain.api.param.DropParam;
import com.alibaba.dbhub.server.domain.api.param.SchemaOperationParam;
import com.alibaba.dbhub.server.domain.api.param.SchemaQueryParam;
import com.alibaba.dbhub.server.domain.api.param.ShowCreateTableParam;
import com.alibaba.dbhub.server.domain.api.param.TablePageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.TableQueryParam;
import com.alibaba.dbhub.server.domain.api.param.TableSelector;
import com.alibaba.dbhub.server.domain.api.service.DatabaseService;
import com.alibaba.dbhub.server.domain.api.service.DlTemplateService;
import com.alibaba.dbhub.server.domain.api.service.TableService;
import com.alibaba.dbhub.server.domain.support.model.Schema;
import com.alibaba.dbhub.server.domain.support.model.Table;
import com.alibaba.dbhub.server.domain.support.model.TableColumn;
import com.alibaba.dbhub.server.domain.support.model.TableIndex;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.web.WebPageResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.aspect.ConnectionInfoAspect;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.converter.RdbWebConverter;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.DdlExportRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableCreateDdlQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableDeleteRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableDetailQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableModifySqlRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.TableUpdateDdlQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.UpdateDatabaseRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.UpdateSchemaRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.ColumnVO;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.IndexVO;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.SchemaVO;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.SqlVO;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.TableVO;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mysql表运维类
 *
 * @author moji
 * @version MysqlTableManageController.java, v 0.1 2022年09月16日 17:41 moji Exp $
 * @date 2022/09/16
 */
@BusinessExceptionAspect
@ConnectionInfoAspect
@RequestMapping("/api/rdb/ddl")
@RestController
public class RdbDdlController {

    @Autowired
    private TableService tableService;

    @Autowired
    private DlTemplateService dlTemplateService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DatabaseService databaseService;

    /**
     * 查询当前DB下的表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public WebPageResult<TableVO> list(TableBriefQueryRequest request) {
        TablePageQueryParam queryParam = rdbWebConverter.tablePageRequest2param(request);
        TableSelector tableSelector = new TableSelector();
        tableSelector.setColumnList(false);
        tableSelector.setIndexList(false);

        PageResult<Table> tableDTOPageResult = tableService.pageQuery(queryParam, tableSelector);
        List<TableVO> tableVOS = rdbWebConverter.tableDto2vo(tableDTOPageResult.getData());
        return WebPageResult.of(tableVOS, tableDTOPageResult.getTotal(), request.getPageNo(),
            request.getPageSize());
    }

    /**
     * 查询数据库里包含的schema_list
     *
     * @param request
     * @return
     */
    @GetMapping("/schema_list")
    public ListResult<SchemaVO> schemaList(DataSourceBaseRequest request) {
        SchemaQueryParam queryParam = SchemaQueryParam.builder().dataBaseName(request.getDatabaseName()).build();
        ListResult<Schema> tableColumns = databaseService.querySchema(queryParam);
        List<SchemaVO> tableVOS = rdbWebConverter.schemaDto2vo(tableColumns.getData());
        return ListResult.of(tableVOS);
    }

    /**
     * 删除数据库
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_database")
    public ActionResult deleteDatabase(@RequestBody DataSourceBaseRequest request) {
        DatabaseOperationParam param = DatabaseOperationParam.builder().databaseName(request.getDatabaseName()).build();
        return databaseService.deleteDatabase(param);
    }

    /**
     * 创建database
     *
     * @param request
     * @return
     */
    @PostMapping("/create_database")
    public ActionResult createDatabase(@RequestBody DataSourceBaseRequest request) {
        DatabaseOperationParam param = DatabaseOperationParam.builder().databaseName(request.getDatabaseName()).build();
        return databaseService.createDatabase(param);
    }

    /**
     * 创建database
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_database")
    public ActionResult modifyDatabase(@RequestBody UpdateDatabaseRequest request) {
        DatabaseOperationParam param = DatabaseOperationParam.builder().databaseName(request.getDatabaseName())
            .newDatabaseName(request.getNewDatabaseName()).build();
        return databaseService.modifyDatabase(param);
    }

    /**
     * 删除schema
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_schema")
    public ActionResult deleteSchema(@RequestBody DataSourceBaseRequest request) {
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
    @PostMapping("/create_schema")
    public ActionResult createSchema(@RequestBody DataSourceBaseRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
            .schemaName(request.getSchemaName()).build();
        return databaseService.createSchema(param);
    }

    /**
     * 创建database
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_schema")
    public ActionResult modifySchema(@RequestBody UpdateSchemaRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
            .schemaName(request.getSchemaName()).newSchemaName(request.getNewSchemaName()).build();
        return databaseService.modifySchema(param);
    }

    /**
     * 查询当前DB下的表columns
     * d
     *
     * @param request
     * @return
     */
    @GetMapping("/column_list")
    public ListResult<ColumnVO> columnList(TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        List<TableColumn> tableColumns = tableService.queryColumns(queryParam);
        List<ColumnVO> tableVOS = rdbWebConverter.columnDto2vo(tableColumns);
        return ListResult.of(tableVOS);
    }

    /**
     * 查询当前DB下的表index
     *
     * @param request
     * @return
     */
    @GetMapping("/index_list")
    public ListResult<IndexVO> indexList(TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        List<TableIndex> tableIndices = tableService.queryIndexes(queryParam);
        List<IndexVO> indexVOS = rdbWebConverter.indexDto2vo(tableIndices);
        return ListResult.of(indexVOS);
    }

    /**
     * 查询当前DB下的表key
     *
     * @param request
     * @return
     */
    @GetMapping("/key_list")
    public ListResult<IndexVO> keyList(TableDetailQueryRequest request) {
        // TODO 增加查询key实现
        return ListResult.of(Lists.newArrayList());
    }

    /**
     * 导出建表语句
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public DataResult<String> export(DdlExportRequest request) {
        ShowCreateTableParam param = rdbWebConverter.ddlExport2showCreate(request);
        return tableService.showCreateTable(param);
    }

    /**
     * 建表语句样例
     *
     * @param request
     * @return
     */
    @GetMapping("/create/example")
    public DataResult<String> createExample(TableCreateDdlQueryRequest request) {
        return tableService.createTableExample(request.getDbType());
    }

    /**
     * 更新表语句样例
     *
     * @param request
     * @return
     */
    @GetMapping("/update/example")
    public DataResult<String> updateExample(TableUpdateDdlQueryRequest request) {
        return tableService.alterTableExample(request.getDbType());
    }

    /**
     * 获取表下列和索引等信息
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public DataResult<TableVO> query(TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        TableSelector tableSelector = new TableSelector();
        tableSelector.setColumnList(true);
        tableSelector.setIndexList(true);
        DataResult<Table> tableDTODataResult = tableService.query(queryParam, tableSelector);
        TableVO tableVO = rdbWebConverter.tableDto2vo(tableDTODataResult.getData());
        return DataResult.of(tableVO);
    }

    /**
     * 获取修改表的sql语句
     *
     * @param request
     * @return
     */
    @GetMapping("/modify/sql")
    public ListResult<SqlVO> modifySql(TableModifySqlRequest request) {
        return tableService.buildSql(
                rdbWebConverter.tableRequest2param(request.getOldTable()),
                rdbWebConverter.tableRequest2param(request.getNewTable()))
            .map(rdbWebConverter::dto2vo);
    }

    /**
     * 删除表
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ActionResult delete(@RequestBody TableDeleteRequest request) {
        DropParam dropParam = rdbWebConverter.tableDelete2dropParam(request);
        return tableService.drop(dropParam);
    }
}
