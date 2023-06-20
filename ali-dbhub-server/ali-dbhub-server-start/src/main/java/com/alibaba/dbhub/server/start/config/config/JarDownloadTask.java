/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.start.config.config;

import javax.annotation.Resource;

import com.alibaba.dbhub.server.domain.support.sql.DbhubContext;
import com.alibaba.dbhub.server.domain.support.util.JdbcJarUtils;
import com.alibaba.dbhub.server.tools.common.config.AliDbhubProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author jipengfei
 * @version : JarDownloadTask.java
 */
@Component
@Slf4j
public class JarDownloadTask implements CommandLineRunner {

    @Resource
    private AliDbhubProperties aliDbhubProperties;

    @Override
    public void run(String... args) throws Exception {
        DbhubContext.JDBC_JAR_DOWNLOAD_URL_LIST = aliDbhubProperties.getJdbcJarDownLoadUrls();
        JdbcJarUtils.asyncDownload(aliDbhubProperties.getJdbcJarDownLoadUrls());
    }
}