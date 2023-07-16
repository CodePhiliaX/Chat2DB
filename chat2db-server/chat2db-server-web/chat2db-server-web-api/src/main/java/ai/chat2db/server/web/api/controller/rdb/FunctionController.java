package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.FunctionService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.spi.model.Function;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/function")
@RestController
public class FunctionController {

    @Autowired
    private FunctionService functionService;


    @GetMapping("/list")
    public ListResult<Function> list(@Valid TableBriefQueryRequest request) {
        return functionService.functions(request.getDatabaseName(), request.getSchemaName());
    }
}
