package ai.chat2db.server.web.api.controller.rdb;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.chat2db.server.domain.api.param.CreateVirtualFKParam;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.api.param.TypeQueryParam;
import ai.chat2db.server.domain.api.param.UpdateVirtualFKParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.BatchTableModifySqlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.BatchTableOperationRequest;
import ai.chat2db.server.web.api.controller.rdb.request.CreateVirtualFKRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.ForeignKeySyncRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableCreateDdlQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDeleteRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDetailQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableModifySqlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableUpdateDdlQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TypeQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.UpdateVirtualFKRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.ColumnVO;
import ai.chat2db.server.web.api.controller.rdb.vo.ExecuteResultVO;
import ai.chat2db.server.web.api.controller.rdb.vo.IndexVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SqlVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SyncResult;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.spi.model.SimpleTable;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableMeta;
import ai.chat2db.spi.model.Type;
import ai.chat2db.spi.model.VirtualForeignKey;
import ai.chat2db.spi.model.ExecuteResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConnectionInfoAspect
@RequestMapping("/api/rdb/table")
@RestController
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    /**
     * 查询当前DB下的表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public WebPageResult<TableVO> list(@Valid TableBriefQueryRequest request) {
        TablePageQueryParam queryParam = rdbWebConverter.tablePageRequest2param(request);
        TableSelector tableSelector = new TableSelector();
        tableSelector.setColumnList(false);
        tableSelector.setIndexList(false);
        PageResult<Table> tableDTOPageResult = tableService.pageQuery(queryParam, tableSelector);
        List<TableVO> tableVOS = rdbWebConverter.tableDto2vo(tableDTOPageResult.getData());
        return WebPageResult.of(tableVOS, tableDTOPageResult.getTotal(), request.getPageNo(),
                request.getPageSize(), queryParam.getLastDocId());
    }

    /**
     * 查询当前DB下的表列表
     *
     * @param request
     * @return
     */
    @GetMapping("/table_list")
    public ListResult<SimpleTable> tableList(@Valid TableBriefQueryRequest request) {
        TablePageQueryParam queryParam = rdbWebConverter.tablePageRequest2param(request);
        return tableService.queryTables(queryParam);

    }


    /**
     * 查询当前DB下的表columns
     * d
     *
     * @param request
     * @return
     */
    @GetMapping("/column_list")
    public ListResult<ColumnVO> columnList(@Valid TableDetailQueryRequest request) {
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
    public ListResult<IndexVO> indexList(@Valid TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        List<TableIndex> tableIndices = tableService.queryIndexes(queryParam);
        List<IndexVO> indexVOS = rdbWebConverter.indexDto2vo(tableIndices);
        return ListResult.of(indexVOS);
    }

    /**
     * 导出建表语句
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public DataResult<String> export(@Valid DdlExportRequest request) {
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
    public DataResult<String> createExample(@Valid TableCreateDdlQueryRequest request) {
        return tableService.createTableExample(request.getDbType());
    }

    /**
     * 更新表语句样例
     *
     * @param request
     * @return
     */
    @GetMapping("/update/example")
    public DataResult<String> updateExample(@Valid TableUpdateDdlQueryRequest request) {
        return tableService.alterTableExample(request.getDbType());
    }

    /**
     * 获取表下列和索引等信息
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public DataResult<Table> query(@Valid TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        TableSelector tableSelector = new TableSelector();
        tableSelector.setColumnList(true);
        tableSelector.setIndexList(true);
        return tableService.query(queryParam, tableSelector);
        //TableVO tableVO = rdbWebConverter.tableDto2vo(tableDTODataResult.getData());
        //return DataResult.of(tableVO);
    }

    /**
     * 获取修改表的sql语句
     *
     * @param request
     * @return
     */
    @PostMapping("/modify/sql")
    public ListResult<SqlVO> modifySql(@Valid @RequestBody TableModifySqlRequest request) {
        Table table = rdbWebConverter.tableRequest2param(request.getNewTable());
        table.setSchemaName(request.getSchemaName());
        table.setDatabaseName(request.getDatabaseName());
        for (TableColumn tableColumn : table.getColumnList()) {
            tableColumn.setSchemaName(request.getSchemaName());
            tableColumn.setTableName(table.getName());
            tableColumn.setDatabaseName(request.getDatabaseName());
        }
        for (TableIndex tableIndex : table.getIndexList()) {
            tableIndex.setSchemaName(request.getSchemaName());
            tableIndex.setTableName(table.getName());
            tableIndex.setDatabaseName(request.getDatabaseName());
        }
        return tableService.buildSql(rdbWebConverter.tableRequest2param(request.getOldTable()), table)
                .map(rdbWebConverter::dto2vo);
    }

    /**
     * 批量获取修改表的sql语句
     *
     * @param request
     * @return
     */
    @PostMapping("/batch/modify/sql")
    public ListResult<String> batchModifySql(@Valid @RequestBody BatchTableModifySqlRequest request) {
        return tableService.buildBatchSql(request.getOldTables(), request.getNewTables());
    }

    /**
     * 数据库支持的数据类型
     *
     * @param request
     * @return
     */
    @GetMapping("/type_list")
    public ListResult<Type> types(@Valid TypeQueryRequest request) {
        TypeQueryParam typeQueryParam = TypeQueryParam.builder().dataSourceId(request.getDataSourceId()).build();
        List<Type> types = tableService.queryTypes(typeQueryParam);
        return ListResult.of(types);
    }


    @GetMapping("/table_meta")
    public DataResult<TableMeta> tableMeta(@Valid TypeQueryRequest request) {
        TypeQueryParam typeQueryParam = TypeQueryParam.builder().dataSourceId(request.getDataSourceId()).build();
        TableMeta tableMeta = tableService.queryTableMeta(typeQueryParam);
        return DataResult.of(tableMeta);
    }

    /**
     * 删除表
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ActionResult delete(@Valid @RequestBody TableDeleteRequest request) {
        DropParam dropParam = rdbWebConverter.tableDelete2dropParam(request);
        return tableService.drop(dropParam);
    }

    /**
     * 批量优化表
     *
     * @param request
     * @return
     */
    @PostMapping("/batch/optimize")
    public ListResult<ExecuteResultVO> batchOptimize(@Valid @RequestBody BatchTableOperationRequest request) {
        ListResult<ExecuteResult> results = tableService.batchOptimizeTables(
                request.getTableNames(), request.getDatabaseName(), request.getSchemaName());
        List<ExecuteResultVO> voList = results.getData().stream().map(this::executeResult2vo).toList();
        return ListResult.of(voList);
    }

    /**
     * 批量分析表
     *
     * @param request
     * @return
     */
    @PostMapping("/batch/analyze")
    public ListResult<ExecuteResultVO> batchAnalyze(@Valid @RequestBody BatchTableOperationRequest request) {
        ListResult<ExecuteResult> results = tableService.batchAnalyzeTables(
                request.getTableNames(), request.getDatabaseName(), request.getSchemaName());
        List<ExecuteResultVO> voList = results.getData().stream().map(this::executeResult2vo).toList();
        return ListResult.of(voList);
    }

    private ExecuteResultVO executeResult2vo(ExecuteResult result) {
        ExecuteResultVO vo = new ExecuteResultVO();
        vo.setSql(result.getSql());
        vo.setOriginalSql(result.getOriginalSql());
        vo.setDescription(result.getDescription());
        vo.setMessage(result.getMessage());
        vo.setSuccess(result.getSuccess());
        vo.setUpdateCount(result.getUpdateCount());
        vo.setHeaderList(result.getHeaderList());
        vo.setDataList(result.getDataList());
        vo.setSqlType(result.getSqlType());
        vo.setHasNextPage(result.getHasNextPage());
        vo.setPageNo(result.getPageNo());
        vo.setPageSize(result.getPageSize());
        vo.setFuzzyTotal(result.getFuzzyTotal());
        vo.setDuration(result.getDuration());
        vo.setTableName(result.getTableName());
        vo.setVkSuggestions(result.getVkSuggestions());
        return vo;
    }

}
