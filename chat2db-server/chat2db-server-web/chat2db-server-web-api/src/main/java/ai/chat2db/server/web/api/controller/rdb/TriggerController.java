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

import java.util.List;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/trigger")
@RestController
public class TriggerController {

    @Autowired
    private TriggerService triggerService;

    @GetMapping("/list")
    public WebPageResult<Trigger> list(@Valid TriggerPageRequest request) {
        List<Trigger> list = triggerService.triggersWithCache(request.getDataSourceId(),
                request.getDatabaseName(), request.getSchemaName(), request.getSearchKey(), request.isRefresh());
        Long total = CollectionUtils.isNotEmpty(list) ? Long.valueOf(list.size()) : 0L;
        Integer pageSize = list != null ? list.size() : 0;
        return WebPageResult.of(list, total, 1, pageSize);
    }

    @GetMapping("/detail")
    public DataResult<Trigger> detail(@Valid TriggerDetailRequest request) {
        return DataResult.of(triggerService.detail(request.getDatabaseName(), request.getSchemaName(), request.getTriggerName()));
    }
}
