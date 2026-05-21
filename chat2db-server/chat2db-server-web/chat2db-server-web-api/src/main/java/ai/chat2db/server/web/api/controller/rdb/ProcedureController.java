package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.ProcedureService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.ProcedureDetailRequest;
import ai.chat2db.server.web.api.controller.rdb.request.ProcedurePageRequest;
import ai.chat2db.spi.model.Procedure;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/procedure")
@RestController
public class ProcedureController {

    @Autowired
    private ProcedureService procedureService;

    @GetMapping("/list")
    public WebPageResult<Procedure> list(@Valid ProcedurePageRequest request) {
        List<Procedure> procedureList = procedureService.proceduresWithCache(request.getDataSourceId(),
                request.getDatabaseName(), request.getSchemaName(), request.getSearchKey(), request.isRefresh());
        return WebPageResult.of(procedureList, Long.valueOf(procedureList.size()), 1,
            procedureList.size());
    }

    @GetMapping("/detail")
    public DataResult<Procedure> detail(@Valid ProcedureDetailRequest request) {
        return DataResult.of(procedureService.detail(request.getDatabaseName(), request.getSchemaName(), request.getProcedureName()));
    }
}
