package com.alibaba.dbhub.server.domain.api.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.DataSourceCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceSelector;
import com.alibaba.dbhub.server.domain.api.param.DataSourceUpdateParam;
import com.alibaba.dbhub.server.domain.support.model.Database;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;

import com.jcraft.jsch.JSchException;

/**
 * 数据源管理服务
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 2022年09月23日 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DataSourceService {

    /**
     * 创建数据源连接
     *
     * @param param
     * @return
     */
    DataResult<Long> create(DataSourceCreateParam param);

    /**
     * 更新数据源连接
     *
     * @param param
     * @return
     */
    ActionResult update(DataSourceUpdateParam param);

    /**
     * 删除数据源连接
     *
     * @param id
     * @return
     */
    ActionResult delete(@NotNull Long id);

    /**
     * 根据id查询数据源连接详情
     *
     * @param id
     * @return
     */
    DataResult<DataSource> queryById(@NotNull Long id);

    /**
     * 克隆连接
     *
     * @param id
     * @return
     */
    DataResult<Long> copyById(@NotNull Long id);

    /**
     * 分页查询数据源列表
     *
     * @param param
     * @param selector
     * @return
     */
    PageResult<DataSource> queryPage(DataSourcePageQueryParam param, DataSourceSelector selector);

    /**
     * 通过ID列表查询数据源
     *
     * @param ids
     * @return
     */
    ListResult<DataSource> queryByIds(List<Long>ids);

    /**
     * 数据源连接测试
     *
     * @param param
     * @return
     */
    ActionResult preConnect(DataSourcePreConnectParam param);

    /**
     * 连接数据源
     *
     * @param id
     * @return
     */
    ListResult<Database> connect(Long id);

    /**
     * 关闭数据源连接
     *
     * @param id
     * @return
     */
    ActionResult close(Long id);

}
