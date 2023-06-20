/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.core.converter;

import com.alibaba.dbhub.server.domain.api.model.Config;
import com.alibaba.dbhub.server.domain.api.param.SystemConfigParam;
import com.alibaba.dbhub.server.domain.repository.entity.SystemConfigDO;

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