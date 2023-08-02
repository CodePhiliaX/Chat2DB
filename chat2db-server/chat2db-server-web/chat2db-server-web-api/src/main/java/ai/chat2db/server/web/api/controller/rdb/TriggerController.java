package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.TriggerService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.spi.model.Trigger;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/trigger")
@RestController
public class TriggerController {

    @Autowired
    private TriggerService triggerService;


    @GetMapping("/list")
    public ListResult<Trigger> list(@Valid TableBriefQueryRequest request) {
        return triggerService.triggers(request.getDatabaseName(), request.getSchemaName());
    }
}
