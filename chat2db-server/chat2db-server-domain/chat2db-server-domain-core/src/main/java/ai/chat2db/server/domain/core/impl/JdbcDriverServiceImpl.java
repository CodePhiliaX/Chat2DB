package ai.chat2db.server.domain.core.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ai.chat2db.server.domain.api.service.JdbcDriverService;
import ai.chat2db.server.domain.repository.entity.JdbcDriverDO;
import ai.chat2db.server.domain.repository.mapper.JdbcDriverMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.JdbcJarUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JdbcDriverServiceImpl implements JdbcDriverService {

    @Autowired
    private JdbcDriverMapper jdbcDriverMapper;

    @Override
    public DataResult<DBConfig> getDrivers(String dbType) {
        Map<String, DriverConfig> driverConfigMap = new LinkedHashMap<>();
        LambdaQueryWrapper<JdbcDriverDO> query = new LambdaQueryWrapper<JdbcDriverDO>();
        query.eq(JdbcDriverDO::getDbType, dbType);
        List<JdbcDriverDO> driverDOS = jdbcDriverMapper.selectList(query);
        if (!CollectionUtils.isEmpty(driverDOS)) {
            for (JdbcDriverDO driverConfig : driverDOS) {
                String[] jarPaths = driverConfig.getJdbcDriver().split(",");
                boolean flag = true;
                for (String jarPath : jarPaths) {
                    File file = new File(JdbcJarUtils.PATH + jarPath);
                    if (!file.exists()) {
                        flag = false;
                        break;
                    }
                }
                if (flag && driverConfigMap.get(driverConfig.getJdbcDriver()) == null) {
                    DriverConfig dc = new DriverConfig();
                    dc.setCustom(true);
                    dc.setDbType(driverConfig.getDbType());
                    dc.setJdbcDriver(driverConfig.getJdbcDriver());
                    dc.setJdbcDriverClass(driverConfig.getJdbcDriverClass());
                    driverConfigMap.put(driverConfig.getJdbcDriver(), dc);
                } else {
                    log.warn("Driver file not found: {}", driverConfig.getJdbcDriver());
                }
            }
        }

        DBConfig dbConfig = Chat2DBContext.PLUGIN_MAP.get(dbType).getDBConfig();
        List<DriverConfig> driverConfigList = dbConfig.getDriverConfigList();
        for (DriverConfig driverConfig : driverConfigList) {
            String[] jarPaths = driverConfig.getJdbcDriver().split(",");
            boolean flag = true;
            for (String jarPath : jarPaths) {
                File file = new File(JdbcJarUtils.PATH + jarPath);
                if (!file.exists()) {
                    flag = false;
                    break;
                }
            }
            if (flag && driverConfigMap.get(driverConfig.getJdbcDriver()) == null) {
                driverConfigMap.put(driverConfig.getJdbcDriver(), driverConfig);
            } else {
                log.warn("Driver file not found: {}", driverConfig.getJdbcDriver());
            }
        }
        dbConfig.setDriverConfigList(driverConfigMap.isEmpty() ? null : Lists.newArrayList(driverConfigMap.values()));
        return DataResult.of(dbConfig);
    }

    @Override
    public ActionResult upload(String dbType, String jdbcDriverClass, String localPath) {
        JdbcDriverDO driverDO = new JdbcDriverDO();
        driverDO.setJdbcDriverClass(jdbcDriverClass);
        driverDO.setDbType(dbType);
        driverDO.setJdbcDriver(localPath);
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
