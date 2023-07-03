
package ai.chat2db.server.domain.core.converter;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.repository.entity.SystemConfigDO;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

/**
 * @author jipengfei
 * @version : ConfigConverter.java
 */
@Slf4j
@Mapper(componentModel = "spring")
public abstract class ConfigConverter {

    public abstract SystemConfigDO param2do(SystemConfigParam param);

    public abstract Config do2model(SystemConfigDO systemConfigDO);
}