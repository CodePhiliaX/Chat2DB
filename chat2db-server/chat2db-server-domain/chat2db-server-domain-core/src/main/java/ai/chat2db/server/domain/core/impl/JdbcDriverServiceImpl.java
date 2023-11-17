package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.JdbcDriverService;
import ai.chat2db.server.domain.core.converter.DriverConfigConverter;
import ai.chat2db.server.domain.repository.entity.JdbcDriverDO;
import ai.chat2db.server.domain.repository.mapper.JdbcDriverMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.IDriverManager;
import ai.chat2db.spi.util.JdbcJarUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ai.chat2db.spi.util.JdbcUtils.setDriverDefaultProperty;


@Slf4j
@Service
public class JdbcDriverServiceImpl implements JdbcDriverService {

    @Autowired
    private JdbcDriverMapper jdbcDriverMapper;

    @Autowired
    private DriverConfigConverter driverConfigConverter;

    @Override
    public DataResult<DBConfig> getDrivers(String dbType) {
        Map<String, DriverConfig> driverConfigMap = new LinkedHashMap<>();
        LambdaQueryWrapper<JdbcDriverDO> query = new LambdaQueryWrapper<JdbcDriverDO>();
        query.eq(JdbcDriverDO::getDbType, dbType);
        List<JdbcDriverDO> driverDOS = jdbcDriverMapper.selectList(query);
        List<DriverConfig> driverConfigs = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(driverDOS)) {
            driverConfigs = driverDOS.stream().map(driverConfigConverter::do2Config).collect(Collectors.toList());
        }

        DBConfig dbConfig = Chat2DBContext.PLUGIN_MAP.get(dbType).getDBConfig();
        List<DriverConfig> driverConfigList = dbConfig.getDriverConfigList();
        if (CollectionUtils.isNotEmpty(driverConfigList)) {
            driverConfigs.addAll(driverConfigList);
        }

        for (DriverConfig driverConfig : driverConfigs) {
            boolean flag = driverExists(driverConfig);
            if (flag && driverConfigMap.get(driverConfig.getJdbcDriver()) == null) {
                driverConfigMap.put(driverConfig.getJdbcDriver(), driverConfig);
                //TODO :临时解决方案，后续需要优化
                //setDriverDefaultProperty(driverConfig);
            } else {
                log.warn("Driver file not found: {}", driverConfig.getJdbcDriver());
            }
        }
        dbConfig.setDriverConfigList(driverConfigMap.isEmpty() ? null : Lists.newArrayList(driverConfigMap.values()));
        return DataResult.of(dbConfig);
    }


    private boolean driverExists(DriverConfig driverConfig) {
        boolean flag = true;
        String[] jarPaths = driverConfig.getJdbcDriver().split(",");
        for (String jarPath : jarPaths) {
            File file = new File(JdbcJarUtils.PATH + jarPath);
            if (!file.exists()) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public ActionResult upload(String dbType, String jdbcDriverClass, String localPath) {
        JdbcDriverDO driverDO = new JdbcDriverDO();
        driverDO.setJdbcDriverClass(jdbcDriverClass);
        driverDO.setDbType(dbType);
        driverDO.setJdbcDriver(localPath);
        DriverConfig driverConfig = driverConfigConverter.do2Config(driverDO);
        try {
            IDriverManager.getClassLoader(driverConfig);
        } catch (Exception e) {
            throw new RuntimeException("Driver error,please check the driver file", e);
        }
        jdbcDriverMapper.insert(driverDO);
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult download(String dbType) {
        DBConfig dbConfig = Chat2DBContext.PLUGIN_MAP.get(dbType).getDBConfig();
        List<DriverConfig> driverConfigList = dbConfig.getDriverConfigList();
        for (DriverConfig driverConfig : driverConfigList) {
            List<String> downloadJdbcDriverUrls = driverConfig.getDownloadJdbcDriverUrls();
            for (String downloadJdbcDriverUrl : downloadJdbcDriverUrls) {
                try {
                    JdbcJarUtils.download(downloadJdbcDriverUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ActionResult.isSuccess();
    }
}
