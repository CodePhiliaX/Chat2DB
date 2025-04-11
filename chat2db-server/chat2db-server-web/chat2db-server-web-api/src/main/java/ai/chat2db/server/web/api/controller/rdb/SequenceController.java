package ai.chat2db.server.web.api.controller.rdb;


import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DdlExportRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * sequence controller
 *
 * @author Sylphy
 */
@Slf4j
@RestController
@ConnectionInfoAspect
@RequiredArgsConstructor
@RequestMapping("/api/rdb/sequence")
public class SequenceController {
    private final RdbWebConverter rdbWebConverter;
    private final DatabaseService databaseService;
    private final SequenceService sequenceService;

    /**
     * Export sequence creation statement
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public DataResult<String> export(@Valid DdlExportRequest request) {
        ShowCreateSequenceParam param = rdbWebConverter.ddlExport2showSequenceCreate(request);
        return sequenceService.showCreateSequence(param);
    }
}
