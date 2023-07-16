package ai.chat2db.server.web.api.controller.rdb;

import java.util.List;

import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.spi.model.Table;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/view")
@RestController
public class ViewController {
    @Autowired
    private ViewService viewService;


    @Autowired
    private RdbWebConverter rdbWebConverter;

    @GetMapping("/list")
    public ListResult<TableVO> list(@Valid TableBriefQueryRequest request) {
        ListResult<Table> tableDTOPageResult = viewService.views(request.getDatabaseName(), request.getSchemaName());
        List<TableVO> tableVOS = rdbWebConverter.tableDto2vo(tableDTOPageResult.getData());
        return ListResult.of(tableVOS);
    }
}
