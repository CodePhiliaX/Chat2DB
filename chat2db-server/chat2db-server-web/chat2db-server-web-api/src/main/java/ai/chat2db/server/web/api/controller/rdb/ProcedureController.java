package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.ProcedureService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.spi.model.Procedure;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/procedure")
@RestController
public class ProcedureController {

    private ProcedureService procedureService;

    @GetMapping("/list")
    public ListResult<Procedure> list(@Valid TableBriefQueryRequest request) {
        return procedureService.procedures(request.getDatabaseName(), request.getSchemaName());
    }
}
