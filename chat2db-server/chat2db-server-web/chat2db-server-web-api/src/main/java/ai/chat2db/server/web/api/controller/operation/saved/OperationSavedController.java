package ai.chat2db.server.web.api.controller.operation.saved;

import java.util.List;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.domain.api.service.OperationService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.operation.saved.converter.OperationWebConverter;
import ai.chat2db.server.web.api.controller.operation.saved.request.BatchTabCloseRequest;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationCreateRequest;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationQueryRequest;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationUpdateRequest;
import ai.chat2db.server.web.api.controller.operation.saved.vo.OperationVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * My save service class
 *
 * @author moji
 * @version DdlManageController.java, v 0.1 September 16, 2022 19:59 moji Exp $
 * @date 2022/09/16
 */
@RequestMapping("/api/operation/saved")
@RestController
public class OperationSavedController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private OperationWebConverter operationWebConverter;

    /**
     * Check my saves
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public WebPageResult<OperationVO> list(OperationQueryRequest request) {
        OperationPageQueryParam param = operationWebConverter.queryReq2param(request,ContextUtils.getUserId());
        param.setUserId(ContextUtils.getUserId());
        PageResult<Operation> dtoPageResult = operationService.queryPage(param);
        List<OperationVO> operationVOS = operationWebConverter.dto2vo(dtoPageResult.getData());
        return WebPageResult.of(operationVOS, dtoPageResult.getTotal(), request.getPageNo(), request.getPageSize());
    }

    /**
     * Query console based on id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public DataResult<OperationVO> get(@PathVariable("id") Long id) {
        OperationQueryParam param = new OperationQueryParam();
        param.setId(id);
        param.setUserId(ContextUtils.getUserId());
        return operationService.queryExistent(param).map(operationWebConverter::dto2vo);
    }

    /**
     * Add my save
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody OperationCreateRequest request) {
        OperationSavedParam param = operationWebConverter.req2param(request);
        param.setTabOpened("y");
        return operationService.createWithPermission(param);
    }

    /**
     * Update my save
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody OperationUpdateRequest request) {
        OperationUpdateParam param = operationWebConverter.updateReq2param(request);
        return operationService.updateWithPermission(param);
    }

    /**
     * Close tags in batches
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch_tab_close", method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult batchTabClose(@RequestBody BatchTabCloseRequest request) {
        if (CollectionUtils.isEmpty(request.getIdList())) {
            return ActionResult.isSuccess();
        }
        request.getIdList().forEach(id -> {
            OperationUpdateParam param = new OperationUpdateParam();
            param.setId(id);
            param.setTabOpened("n");
            operationService.updateWithPermission(param);
        });
        return ActionResult.isSuccess();
    }

    /**
     * delete my save
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return operationService.deleteWithPermission(id);
    }
}
