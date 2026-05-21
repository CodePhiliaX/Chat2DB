
package ai.chat2db.server.domain.core.impl;

import java.time.LocalDateTime;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.domain.core.converter.ConfigConverter;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.domain.repository.entity.SystemConfigDO;
import ai.chat2db.server.domain.repository.mapper.ChartMapper;
import ai.chat2db.server.domain.repository.mapper.SystemConfigMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jipengfei
 * @version : ConfigServiceImpl.java
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    private SystemConfigMapper getMapper() {
        return Dbutils.getMapper(SystemConfigMapper.class);
    }

    @Autowired
    private ConfigConverter configConverter;

    @Override
    public void create(SystemConfigParam param) {
        SystemConfigDO systemConfigDO = configConverter.param2do(param);
        systemConfigDO.setGmtCreate(LocalDateTime.now());
        systemConfigDO.setGmtModified(LocalDateTime.now());
        getMapper().insert(systemConfigDO);
        
    }

    @Override
    public void update(SystemConfigParam param) {
        SystemConfigDO systemConfigDO = configConverter.param2do(param);
        UpdateWrapper<SystemConfigDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("code", param.getCode());
        getMapper().update(systemConfigDO, updateWrapper);
        
    }

    @Override
    public void createOrUpdate(SystemConfigParam param) {
        SystemConfigDO systemConfigDO = getMapper().selectOne(
            new UpdateWrapper<SystemConfigDO>().eq("code", param.getCode()));
        if (systemConfigDO == null) {
            create(param);
        } else {
            update(param);
        }
    }

    @Override
    public Config find(String code) {
        SystemConfigDO systemConfigDO = getMapper().selectOne(
            new UpdateWrapper<SystemConfigDO>().eq("code", code));
        return configConverter.do2model(systemConfigDO);
    }

    @Override
    public void delete(String code) {
        getMapper().delete(new UpdateWrapper<SystemConfigDO>().eq("code", code));
        
    }
}