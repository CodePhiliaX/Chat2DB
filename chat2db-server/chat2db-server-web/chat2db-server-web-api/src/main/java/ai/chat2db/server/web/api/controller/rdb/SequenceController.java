package ai.chat2db.server.web.api.controller.rdb;


import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.*;
import ai.chat2db.server.web.api.controller.rdb.vo.SequenceVO;
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
     * Get a SQL statement that modifies or creates a new sequence
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

    /**
     * Delete sequence
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ActionResult delete(@Valid @RequestBody SequenceDeleteRequest request){
        DropParam dropParam = rdbWebConverter.sequenceDelete2dropParam(request);
        return sequenceService.drop(dropParam);
    }

    /**
     * Get information such as table columns and indexes
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public DataResult<SequenceVO> query(@Valid SequenceDetailQueryRequest request) {
        SequenceQueryParam queryParam = rdbWebConverter.sequenceRequest2param(request);
        DataResult<Sequence> sequenceDTODataResult = sequenceService.query(queryParam);
        SequenceVO sequenceVO = rdbWebConverter.sequenceDto2vo(sequenceDTODataResult.getData());
        return DataResult.of(sequenceVO);
    }
}
