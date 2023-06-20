package com.alibaba.dbhub.server.web.api.controller.rdb;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.param.DlExecuteParam;
import com.alibaba.dbhub.server.domain.api.service.DlTemplateService;
import com.alibaba.dbhub.server.domain.support.model.ExecuteResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.aspect.ConnectionInfoAspect;
import com.alibaba.dbhub.server.web.api.controller.rdb.converter.RdbWebConverter;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.DataExportRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.DdlCountRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.request.DmlRequest;
import com.alibaba.dbhub.server.web.api.controller.rdb.vo.ExecuteResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * mysql数据运维类
 *
 * @author moji
 * @version MysqlDataManageController.java, v 0.1 2022年09月16日 17:37 moji Exp $
 * @date 2022/09/16
 */
@BusinessExceptionAspect
@ConnectionInfoAspect
@RequestMapping("/api/rdb/dml")
@RestController
public class RdbDmlController {

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;

    /**
     * 导出结果集Excel
     *
     * @param request
     * @return
     */
    @GetMapping("/export/excel")
    public ActionResult export(DataExportRequest request) {
        return null;
    }

    /**
     * 导出结果集Insert
     *
     * @param request
     * @return
     */
    @GetMapping("/export/insert")
    public ActionResult exportInsert(DataExportRequest request) {
        return null;
    }

    /**
     * 导出选中行Insert
     *
     * @param request
     * @return
     */
    @GetMapping("/export/insert/selected")
    public ActionResult exportInsertSelected(DataExportRequest request) {
        return null;
    }

    /**
     * 增删改查等数据运维
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute",method = {RequestMethod.POST, RequestMethod.PUT})
    public ListResult<ExecuteResultVO> manage(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        ListResult<ExecuteResult> resultDTOListResult = dlTemplateService.execute(param);
        List<ExecuteResultVO> resultVOS = rdbWebConverter.dto2vo(resultDTOListResult.getData());
        return ListResult.of(resultVOS);
    }

    /**
     * 统计行的数量
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/count", method = {RequestMethod.POST, RequestMethod.PUT})
    public DataResult<Long> count(@RequestBody DdlCountRequest request) {
        return dlTemplateService.count(rdbWebConverter.request2param(request));
    }

}
