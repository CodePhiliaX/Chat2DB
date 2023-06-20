package com.alibaba.dbhub.server.web.api.controller.data.source;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.ConsoleCloseParam;
import com.alibaba.dbhub.server.domain.api.param.ConsoleConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceSelector;
import com.alibaba.dbhub.server.domain.api.param.DataSourceUpdateParam;
import com.alibaba.dbhub.server.domain.api.service.ConsoleService;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.support.model.Database;
import com.alibaba.dbhub.server.domain.support.sql.SSHManager;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.web.WebPageResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.aspect.ConnectionInfoAspect;
import com.alibaba.dbhub.server.web.api.controller.data.source.converter.DataSourceWebConverter;
import com.alibaba.dbhub.server.web.api.controller.data.source.converter.SSHWebConverter;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.ConsoleCloseRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.ConsoleConnectRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceAttachRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceCloneRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceCloseRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceCreateRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceTestRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.SSHTestRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.vo.DataSourceVO;
import com.alibaba.dbhub.server.web.api.controller.data.source.vo.DatabaseVO;

import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
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
 * 数据库连接类
 *
 * @author moji
 * @version ConnectionController.java, v 0.1 2022年09月16日 14:07 moji Exp $
 * @date 2022/09/16
 */
@BusinessExceptionAspect
@ConnectionInfoAspect
@RequestMapping("/api/connection")
@RestController
@Slf4j
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private DataSourceWebConverter dataSourceWebConverter;

    @Autowired
    private SSHWebConverter sshWebConverter;

    /**
     * 数据库连接测试
     *
     * @param request
     * @return
     */
    @RequestMapping("/datasource/pre_connect")
    public ActionResult preConnect(@RequestBody DataSourceTestRequest request) {
        DataSourcePreConnectParam param = dataSourceWebConverter.testRequest2param(request);
        return dataSourceService.preConnect(param);
    }

    /**
     * 数据库连接测试
     *
     * @param request
     * @return
     */
    @RequestMapping("/ssh/pre_connect")
    public ActionResult sshConnect(@RequestBody SSHTestRequest request) {
        Session session = null;
        try {
            session = SSHManager.getSSHSession(sshWebConverter.toInfo(request));
        } catch (Exception e) {
            log.error("sshConnect error", e);
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return ActionResult.isSuccess();
    }

    /**
     * 数据库连接
     *
     * @param request
     * @return
     */
    @GetMapping("/datasource/connect")
    public ListResult<DatabaseVO> attach(@Valid @NotNull DataSourceAttachRequest request) {
        ListResult<Database> databaseDTOListResult = dataSourceService.connect(request.getId());
        List<DatabaseVO> databaseVOS = dataSourceWebConverter.databaseDto2vo(databaseDTOListResult.getData());
        return ListResult.of(databaseVOS);
    }

    /**
     * 关闭数据库连接
     *
     * @param request
     * @return
     */
    @GetMapping("/datasource/close")
    public ActionResult close(@Valid @NotNull DataSourceCloseRequest request) {
        return dataSourceService.close(request.getId());
    }

    /**
     * Console连接
     *
     * @param request
     * @return
     */
    @GetMapping("/console/connect")
    public ActionResult connect(@Valid @NotNull ConsoleConnectRequest request) {
        ConsoleConnectParam consoleConnectParam = dataSourceWebConverter.request2connectParam(request);
        return consoleService.createConsole(consoleConnectParam);
    }

    /**
     * 关闭Console连接
     *
     * @param request
     * @return
     */
    @GetMapping("/console/close")
    public ActionResult closeConsole(@Valid @NotNull ConsoleCloseRequest request) {
        ConsoleCloseParam closeParam = dataSourceWebConverter.request2closeParam(request);
        return consoleService.closeConsole(closeParam);
    }

    /**
     * 查询我建立的数据库连接
     *
     * @param request
     * @return
     */
    @GetMapping("/datasource/list")
    public WebPageResult<DataSourceVO> list(DataSourceQueryRequest request) {
        DataSourcePageQueryParam param = dataSourceWebConverter.queryReq2param(request);
        PageResult<DataSource> result = dataSourceService.queryPage(param, new DataSourceSelector());
        List<DataSourceVO> dataSourceVOS = dataSourceWebConverter.dto2vo(result.getData());
        return WebPageResult.of(dataSourceVOS, result.getTotal(), result.getPageNo(), result.getPageSize());
    }

    /**
     * 获取连接内容
     *
     * @param id
     * @return
     */
    @GetMapping("/datasource/{id}")
    public DataResult<DataSourceVO> queryById(@PathVariable("id") Long id) {
        DataResult<DataSource> dataResult = dataSourceService.queryById(id);
        DataSourceVO dataSourceVO = dataSourceWebConverter.dto2vo(dataResult.getData());
        return DataResult.of(dataSourceVO);
    }

    /**
     * 保存连接
     *
     * @param request
     * @return
     */
    @PostMapping("/datasource/create")
    public DataResult<Long> create(@RequestBody DataSourceCreateRequest request) {
        DataSourceCreateParam param = dataSourceWebConverter.createReq2param(request);
        return dataSourceService.create(param);
    }

    /**
     * 更新连接
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/datasource/update",method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody DataSourceUpdateRequest request) {
        DataSourceUpdateParam param = dataSourceWebConverter.updateReq2param(request);
        return dataSourceService.update(param);
    }

    /**
     * 克隆连接
     *
     * @param request
     * @return
     */
    @PostMapping("/datasource/clone")
    public DataResult<Long> copy(@RequestBody DataSourceCloneRequest request) {
        return dataSourceService.copyById(request.getId());
    }

    /**
     * 删除连接
     *
     * @param id
     * @return
     */
    @DeleteMapping("/datasource/{id}")
    public ActionResult delete(@PathVariable Long id) {
        return dataSourceService.delete(id);
    }

}
