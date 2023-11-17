package ai.chat2db.server.web.api.controller.rdb;

import java.sql.Connection;
import java.util.List;

import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.param.UpdateSelectResultParam;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DdlCountRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DmlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.SelectResultUpdateRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.ExecuteResultVO;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
@ConnectionInfoAspect
@RequestMapping("/api/rdb/dml")
@RestController
public class RdbDmlController {

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;

    /**
     * 增删改查等数据运维
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute", method = {RequestMethod.POST, RequestMethod.PUT})
    public ListResult<ExecuteResultVO> manage(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        ListResult<ExecuteResult> resultDTOListResult = dlTemplateService.execute(param);
        List<ExecuteResultVO> resultVOS = rdbWebConverter.dto2vo(resultDTOListResult.getData());
        return ListResult.of(resultVOS);
    }


    /**
     * update 查询结果
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute_update", method = {RequestMethod.POST, RequestMethod.PUT})
    public  DataResult<ExecuteResultVO> executeSelectResultUpdate(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        DataResult<ExecuteResult>  result = dlTemplateService.executeUpdate(param);
        if(!result.success()){
            return DataResult.error(result.getErrorCode(),result.getErrorMessage());
        }
       return DataResult.of(rdbWebConverter.dto2vo(result.getData()));

    }
    @RequestMapping(value = "/get_update_sql", method = {RequestMethod.POST, RequestMethod.PUT})
    public DataResult<String> getUpdateSelectResultSql(@RequestBody SelectResultUpdateRequest request) {
        UpdateSelectResultParam param = rdbWebConverter.request2param(request);
        return dlTemplateService.updateSelectResult(param);
    }


    /**
     * 增删改查等数据运维
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute_ddl", method = {RequestMethod.POST, RequestMethod.PUT})
    public DataResult<ExecuteResultVO> executeDDL(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        Connection connection = Chat2DBContext.getConnection();
        if (connection != null) {
            try {
                boolean flag = true;
                ExecuteResultVO executeResult = null;
                //connection.setAutoCommit(false);
                ListResult<ExecuteResult> resultDTOListResult = dlTemplateService.execute(param);
                List<ExecuteResultVO> resultVOS = rdbWebConverter.dto2vo(resultDTOListResult.getData());
                if (!CollectionUtils.isEmpty(resultVOS)) {
                    for (ExecuteResultVO resultVO : resultVOS) {
                        if (!resultVO.getSuccess()) {
                            flag = false;
                            executeResult = resultVO;
                            break;

                        }
                    }
                }
                if (flag) {
                    //connection.commit();
                    return DataResult.of(resultVOS.get(0));
                }else {
                    //connection.rollback();
                    return DataResult.of(executeResult);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            return DataResult.error("connection error", "");
        }
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
