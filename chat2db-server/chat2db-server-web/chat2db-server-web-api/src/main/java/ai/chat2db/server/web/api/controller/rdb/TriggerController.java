package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.TriggerService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.TriggerDetailRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TriggerPageRequest;
import ai.chat2db.spi.model.Trigger;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
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
    public WebPageResult<Trigger> list(@Valid TriggerPageRequest request) {
        ListResult<Trigger> listResult = triggerService.triggers(request.getDatabaseName(), request.getSchemaName());
        Long total = CollectionUtils.isNotEmpty(listResult.getData()) ? Long.valueOf(listResult.getData().size()) : 0L;
        return WebPageResult.of(listResult.getData(), total, 1,
                listResult.getData().size());
    }

    @GetMapping("/detail")
    public DataResult<Trigger> detail(@Valid TriggerDetailRequest request) {
        return triggerService.detail(request.getDatabaseName(), request.getSchemaName(), request.getTriggerName());
    }
}
