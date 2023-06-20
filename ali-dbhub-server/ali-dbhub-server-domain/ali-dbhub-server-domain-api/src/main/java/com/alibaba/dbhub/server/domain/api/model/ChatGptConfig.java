package com.alibaba.dbhub.server.domain.api.model;

import lombok.Data;

/**
 * @author moji
 * @version ChatGptConfig.java, v 0.1 2023年05月09日 13:47 moji Exp $
 * @date 2023/05/09
 */
@Data
public class ChatGptConfig {

    /**
     * OpenAi APIKEY
     */
    private String apiKey;

    /**
     * OpenAi APIHOST
     */
    private String apiHost;

    /**
     * http代理Host
     */
    private String httpProxyHost;

    /**
     * http代理Port
     */
    private String httpProxyPort;

    /**
     * AI类型
     * @see com.alibaba.dbhub.server.domain.api.enums.AiSqlSourceEnum
     */
    private String aiSqlSource;

    /**
     * 自定义AI接口
     */
    private String restAiUrl;

    /**
     * Rest接口是否流式输出
     * 非必填，默认值为TRUE
     */
    private Boolean restAiStream = Boolean.TRUE;
}
