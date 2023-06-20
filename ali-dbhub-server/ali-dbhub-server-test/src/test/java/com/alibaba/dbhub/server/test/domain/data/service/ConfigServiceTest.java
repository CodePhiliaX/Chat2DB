/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.test.domain.data.service;

import com.alibaba.dbhub.server.domain.api.param.SystemConfigParam;
import com.alibaba.dbhub.server.domain.api.service.ConfigService;
import com.alibaba.dbhub.server.test.common.BaseTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jipengfei
 * @version : ConfihServiceTest.java
 */
@Slf4j
public class ConfigServiceTest extends BaseTest {

    @Autowired
    private ConfigService configService;

    @Test
    public void testCreate() {
        SystemConfigParam systemConfigParam = new SystemConfigParam();
        systemConfigParam.setCode("test");
        systemConfigParam.setContent("test1");
        configService.createOrUpdate(systemConfigParam);
    }
}