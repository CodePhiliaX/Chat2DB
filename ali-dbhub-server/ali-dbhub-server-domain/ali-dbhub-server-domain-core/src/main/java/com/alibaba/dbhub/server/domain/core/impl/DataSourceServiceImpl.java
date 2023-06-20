package com.alibaba.dbhub.server.domain.core.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.DataSourceCloseParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceSelector;
import com.alibaba.dbhub.server.domain.api.param.DataSourceTestParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceUpdateParam;
import com.alibaba.dbhub.server.domain.api.param.DatabaseQueryAllParam;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.core.converter.DataSourceConverter;
import com.alibaba.dbhub.server.domain.repository.entity.DataSourceDO;
import com.alibaba.dbhub.server.domain.repository.mapper.DataSourceMapper;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.DataSourceConnect;
import com.alibaba.dbhub.server.domain.support.model.Database;
import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.sql.DbhubContext;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;
import com.alibaba.dbhub.server.domain.support.util.JdbcUtils;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;
import com.alibaba.dbhub.server.tools.common.util.EasyCollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private DataSourceConverter dataSourceConverter;

    @Override
    public DataResult<Long> create(DataSourceCreateParam param) {
        DataSourceDO dataSourceDO = dataSourceConverter.param2do(param);
        dataSourceDO.setGmtCreate(LocalDateTime.now());
        dataSourceDO.setGmtModified(LocalDateTime.now());
        dataSourceMapper.insert(dataSourceDO);
        return DataResult.of(dataSourceDO.getId());
    }

    @Override
    public ActionResult update(DataSourceUpdateParam param) {
        DataSourceDO dataSourceDO = dataSourceConverter.param2do(param);
        dataSourceDO.setGmtModified(LocalDateTime.now());
        dataSourceMapper.updateById(dataSourceDO);
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult delete(Long id) {
        dataSourceMapper.deleteById(id);
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<DataSource> queryById(Long id) {
        DataSourceDO dataSourceDO = dataSourceMapper.selectById(id);
        return DataResult.of(dataSourceConverter.do2dto(dataSourceDO));
    }

    @Override
    public DataResult<Long> copyById(Long id) {
        DataSourceDO dataSourceDO = dataSourceMapper.selectById(id);
        dataSourceDO.setId(null);
        String alias = dataSourceDO.getAlias() + "Copy";
        dataSourceDO.setAlias(alias);
        dataSourceDO.setGmtCreate(LocalDateTime.now());
        dataSourceDO.setGmtModified(LocalDateTime.now());
        dataSourceMapper.insert(dataSourceDO);
        return DataResult.of(dataSourceDO.getId());
    }

    @Override
    public PageResult<DataSource> queryPage(DataSourcePageQueryParam param, DataSourceSelector selector) {
        QueryWrapper<DataSourceDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(param.getSearchKey())) {
            queryWrapper.like("alias", param.getSearchKey());
        }
        Integer start = param.getPageNo();
        Integer offset = param.getPageSize();
        Page<DataSourceDO> page = new Page<>(start, offset);
        IPage<DataSourceDO> iPage = dataSourceMapper.selectPage(page, queryWrapper);
        List<DataSource> dataSources = dataSourceConverter.do2dto(iPage.getRecords());
        return PageResult.of(dataSources, iPage.getTotal(), param);
    }

    @Override
    public ListResult<DataSource> queryByIds(List<Long> ids) {
        List<DataSourceDO> dataSourceDOS = dataSourceMapper.selectBatchIds(ids);
        List<DataSource> dataSources = dataSourceConverter.do2dto(dataSourceDOS);
        return ListResult.of(dataSources);
    }

    @Override
    public ActionResult preConnect(DataSourcePreConnectParam param)  {
        DataSourceTestParam testParam
            = dataSourceConverter.param2param(param);
        DataSourceConnect dataSourceConnect = JdbcUtils.testConnect(testParam.getUrl(), testParam.getHost(),
            testParam.getPort(),
            testParam.getUsername(), testParam.getPassword(), DbTypeEnum.getByName(testParam.getDbType()),
            param.getJdbc(), param.getSsh(), KeyValue.toMap(param.getExtendInfo()));
        if (BooleanUtils.isNotTrue(dataSourceConnect.getSuccess())) {
            return ActionResult.fail(dataSourceConnect.getMessage(), dataSourceConnect.getDescription());
        }
        return ActionResult.isSuccess();
    }

    @Override
    public ListResult<Database> connect(Long id) {
        DatabaseQueryAllParam queryAllParam = new DatabaseQueryAllParam();
        queryAllParam.setDataSourceId(id);
        List<String> databases = DbhubContext.getMetaSchema().databases();
        return ListResult.of(EasyCollectionUtils.toList(databases, name -> Database.builder().name(name).build()));
    }

    @Override
    public ActionResult close(Long id) {
        DataSourceCloseParam closeParam = new DataSourceCloseParam();
        closeParam.setDataSourceId(id);
        SQLExecutor.getInstance().close();
        return ActionResult.isSuccess();
    }

}
