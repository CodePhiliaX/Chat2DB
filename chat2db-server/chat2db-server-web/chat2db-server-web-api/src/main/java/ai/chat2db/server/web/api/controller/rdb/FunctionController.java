package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.service.FunctionService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.FunctionDetailRequest;
import ai.chat2db.server.web.api.controller.rdb.request.FunctionPageRequest;
import ai.chat2db.spi.model.Function;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ConnectionInfoAspect
@RequestMapping("/api/rdb/function")
@RestController
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @GetMapping("/list")
    public WebPageResult<Function> list(@Valid FunctionPageRequest request) {
        List<Function> functionList = functionService.functionsWithCache(request.getDataSourceId(),
                request.getDatabaseName(), request.getSchemaName(), request.getSearchKey(), request.isRefresh());
        return WebPageResult.of(functionList, Long.valueOf(functionList.size()), 1,
            functionList.size());
    }

    @GetMapping("/detail")
    public DataResult<Function> detail(@Valid FunctionDetailRequest request) {
        return DataResult.of(functionService.detail(request.getDatabaseName(), request.getSchemaName(), request.getFunctionName()));
    }
}
