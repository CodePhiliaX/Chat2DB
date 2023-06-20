package com.alibaba.dbhub.server.web.api.controller.operation.saved;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.Operation;
import com.alibaba.dbhub.server.domain.api.param.OperationPageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.OperationSavedParam;
import com.alibaba.dbhub.server.domain.api.param.OperationUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.OperationService;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.web.WebPageResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.controller.operation.saved.converter.OperationWebConverter;
import com.alibaba.dbhub.server.web.api.controller.operation.saved.request.OperationCreateRequest;
import com.alibaba.dbhub.server.web.api.controller.operation.saved.request.OperationQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.operation.saved.request.OperationUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.operation.saved.vo.OperationVO;

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
 * 我的保存服务类
 *
 * @author moji
 * @version DdlManageController.java, v 0.1 2022年09月16日 19:59 moji Exp $
 * @date 2022/09/16
 */
@BusinessExceptionAspect
@RequestMapping("/api/operation/saved")
@RestController
public class OperationSavedController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private OperationWebConverter operationWebConverter;

    /**
     * 查询我的保存
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public WebPageResult<OperationVO> list(OperationQueryRequest request) {
        OperationPageQueryParam param = operationWebConverter.queryReq2param(request);
        PageResult<Operation> dtoPageResult = operationService.queryPage(param);
        List<OperationVO> operationVOS = operationWebConverter.dto2vo(dtoPageResult.getData());
        return WebPageResult.of(operationVOS, dtoPageResult.getTotal(), request.getPageNo(), request.getPageSize());
    }

    /**
     * 根据id查询console
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public DataResult<OperationVO> get(@PathVariable("id") Long id) {
        DataResult<Operation> dtoPageResult = operationService.find(id);
        return DataResult.of(operationWebConverter.dto2vo(dtoPageResult.getData()));
    }

    /**
     * 新增我的保存
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody OperationCreateRequest request) {
        OperationSavedParam param = operationWebConverter.req2param(request);
        param.setTabOpened("y");
        return operationService.create(param);
    }

    /**
     * 更新我的保存
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody OperationUpdateRequest request) {
        OperationUpdateParam param = operationWebConverter.updateReq2param(request);
        return operationService.update(param);
    }

    /**
     * 删除我的保存
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        return operationService.delete(id);
    }
}
