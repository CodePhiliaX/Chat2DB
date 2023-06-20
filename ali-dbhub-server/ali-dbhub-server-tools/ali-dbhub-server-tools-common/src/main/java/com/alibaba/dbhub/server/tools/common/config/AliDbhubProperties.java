package com.alibaba.dbhub.server.tools.common.config;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author moji
 * @version SystemProperties.java, v 0.1 2022年11月13日 14:28 moji Exp $
 * @date 2022/11/13
 */
@Configuration
@ConfigurationProperties(prefix = "ali.dbhub")
@Data
public class AliDbhubProperties {

    /**
     * 版本
     */
    private String version;

    /**
     * jdbc 需要下载的jar包
     */
    private List<String> jdbcJarDownLoadUrls;
}
