package ai.chat2db.server.web.api.controller.rdb;

import java.util.List;

import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDeleteRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDetailQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.ColumnVO;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/view")
@RestController
public class ViewController {
    @Autowired
    private ViewService viewService;

    @Autowired
    private TableService tableService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @GetMapping("/list")
    public WebPageResult<TableVO> list(@Valid TableBriefQueryRequest request) {
        ListResult<Table> tableDTOPageResult = viewService.views(request.getDatabaseName(), request.getSchemaName());
        List<TableVO> tableVOS = rdbWebConverter.tableDto2vo(tableDTOPageResult.getData());
        return WebPageResult.of(tableVOS, Long.valueOf(tableVOS.size()), 1, tableVOS.size());
    }


    @GetMapping("/column_list")
    public ListResult<ColumnVO> columnList(@Valid TableDetailQueryRequest request) {
        TableQueryParam queryParam = rdbWebConverter.tableRequest2param(request);
        List<TableColumn> tableColumns = tableService.queryColumns(queryParam);
        List<ColumnVO> tableVOS = rdbWebConverter.columnDto2vo(tableColumns);
        return ListResult.of(tableVOS);
    }


    @GetMapping("/detail")
    public DataResult<TableVO> detail(@Valid TableDetailQueryRequest request) {
        DataResult<Table> dataResult = viewService.detail(request.getDatabaseName(),request.getSchemaName(),request.getTableName());
        TableVO tableVO = rdbWebConverter.tableDto2vo(dataResult.getData());
        return DataResult.of(tableVO);
    }
    @PostMapping("/delete")
    public ActionResult delete(@Valid TableDeleteRequest request) {
        DropParam dropParam = rdbWebConverter.tableDelete2dropParam(request);
       return tableService.drop(dropParam);
    }

}
