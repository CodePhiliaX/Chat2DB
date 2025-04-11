package ai.chat2db.server.web.api.controller.rdb;


import ai.chat2db.server.domain.api.param.SequencePageQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DdlExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.SequenceBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.SequenceModifySqlRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.SqlVO;
import ai.chat2db.spi.model.*;
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
     * Query the sequence list under the current DB
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public ListResult<SimpleSequence> list(@Valid SequenceBriefQueryRequest request) {
        SequencePageQueryParam queryParam = rdbWebConverter.sequencePageRequest2param(request);
        return sequenceService.pageQuery(queryParam);
    }

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

    /**
     * Get the sql statement that modifies the sequence
     *
     * @param request
     * @return
     */
    @PostMapping("/modify/sql")
    public ListResult<SqlVO> modifySql(@Valid @RequestBody SequenceModifySqlRequest request) {
        Sequence sequence = rdbWebConverter.sequenceRequest2param(request.getNewSequence());
        return sequenceService.buildSql(rdbWebConverter.sequenceRequest2param(request.getOldSequence()), sequence)
                .map(rdbWebConverter::dto2vo);
    }
}
