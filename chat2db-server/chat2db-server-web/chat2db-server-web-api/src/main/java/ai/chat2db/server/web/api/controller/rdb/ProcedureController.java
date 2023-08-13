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

@ConnectionInfoAspect
@RequestMapping("/api/rdb/procedure")
@RestController
public class ProcedureController {

    @Autowired
    private ProcedureService procedureService;

    @GetMapping("/list")
    public WebPageResult<Procedure> list(@Valid ProcedurePageRequest request) {
        ListResult<Procedure> procedureListResult = procedureService.procedures(request.getDatabaseName(),
            request.getSchemaName());
        return WebPageResult.of(procedureListResult.getData(), Long.valueOf(procedureListResult.getData().size()), 1,
            procedureListResult.getData().size());
    }

    @GetMapping("/detail")
    public DataResult<Procedure> detail(@Valid ProcedureDetailRequest request) {
        return procedureService.detail(request.getDatabaseName(), request.getSchemaName(), request.getProcedureName());
    }
}
