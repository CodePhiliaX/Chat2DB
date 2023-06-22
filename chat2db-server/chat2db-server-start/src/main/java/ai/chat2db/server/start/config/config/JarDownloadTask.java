/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.start.config.config;

import ai.chat2db.server.domain.support.sql.DbhubContext;
import ai.chat2db.server.domain.support.util.JdbcJarUtils;
import ai.chat2db.server.tools.common.config.AliDbhubProperties;
import jakarta.annotation.Resource;
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