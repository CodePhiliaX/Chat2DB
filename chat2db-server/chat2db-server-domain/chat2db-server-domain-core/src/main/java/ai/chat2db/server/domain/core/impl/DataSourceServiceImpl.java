package ai.chat2db.server.domain.core.impl;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.DataSourceCloseParam;
import ai.chat2db.server.domain.api.param.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.param.DataSourceSelector;
import ai.chat2db.server.domain.api.param.DataSourceTestParam;
import ai.chat2db.server.domain.api.param.DataSourceUpdateParam;
import ai.chat2db.server.domain.api.param.DatabaseQueryAllParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.core.converter.DataSourceConverter;
import ai.chat2db.server.domain.repository.entity.DataSourceDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.DataSourceConnect;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.IDriverManager;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.JdbcUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Slf4j
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private DataSourceConverter dataSourceConverter;

    @Autowired
    private DatabaseService databaseService;

    @Override
    public DataResult<Long> create(DataSourceCreateParam param) {
        DataSourceDO dataSourceDO = dataSourceConverter.param2do(param);
        dataSourceDO.setGmtCreate(LocalDateTime.now());
        dataSourceDO.setGmtModified(LocalDateTime.now());
        dataSourceMapper.insert(dataSourceDO);
        preWarmingData(dataSourceDO.getId());
        return DataResult.of(dataSourceDO.getId());
    }

    private void preWarmingData(Long dataSourceId) {
        DataResult<DataSource> dataResult = queryById(dataSourceId);
        if (dataResult.success() && dataResult.getData() != null) {
            DataSource dataSource = dataResult.getData();
            DriverConfig driverConfig = dataSource.getDriverConfig();
            if (driverConfig == null || StringUtils.isBlank(driverConfig.getJdbcDriver())) {
                return;
            }
            try (Connection connection = IDriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(),
                dataSource.getPassword(), dataSource.getDriverConfig(), dataSource.getExtendMap())) {
                DatabaseQueryAllParam databaseQueryAllParam = new DatabaseQueryAllParam();
                databaseQueryAllParam.setDataSourceId(dataSourceId);
                databaseQueryAllParam.setConnection(connection);
                databaseQueryAllParam.setDbType(dataSource.getType());
                databaseQueryAllParam.setRefresh(true);
                databaseService.queryAll(databaseQueryAllParam);
            } catch (Exception e) {
                log.error("preWarmingData error", e);
            }
        }
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
    public ActionResult preConnect(DataSourcePreConnectParam param) {
        DataSourceTestParam testParam
            = dataSourceConverter.param2param(param);
        DriverConfig driverConfig = testParam.getDriverConfig();
        if (driverConfig == null || !driverConfig.notEmpty()) {
            driverConfig = Chat2DBContext.getDefaultDriverConfig(param.getType());
        }
        DataSourceConnect dataSourceConnect = JdbcUtils.testConnect(testParam.getUrl(), testParam.getHost(),
            testParam.getPort(),
            testParam.getUsername(), testParam.getPassword(), testParam.getDbType(),
            driverConfig, param.getSsh(), KeyValue.toMap(param.getExtendInfo()));
        if (BooleanUtils.isNotTrue(dataSourceConnect.getSuccess())) {
            return ActionResult.fail(dataSourceConnect.getMessage(), dataSourceConnect.getDescription(),
                dataSourceConnect.getErrorDetail());
        }
        return ActionResult.isSuccess();
    }

    @Override
    public ListResult<Database> connect(Long id) {
        DatabaseQueryAllParam queryAllParam = new DatabaseQueryAllParam();
        queryAllParam.setDataSourceId(id);
        List<Database> databases = Chat2DBContext.getMetaData().databases(Chat2DBContext.getConnection());
        return ListResult.of(databases);
    }

    @Override
    public ActionResult close(Long id) {
        DataSourceCloseParam closeParam = new DataSourceCloseParam();
        closeParam.setDataSourceId(id);
        SQLExecutor.getInstance().close();
        return ActionResult.isSuccess();
    }

}
