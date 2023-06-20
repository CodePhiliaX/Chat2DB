/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller.config.request;

import com.alibaba.dbhub.server.domain.api.enums.AiSqlSourceEnum;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SystemConfigRequest.java
 */
@Data
public class ChatGptSystemConfigRequest {

    /**
     * OpenAi APIKEY
     * 使用OpenAi接口时必填，可前往OpenAI官网查看APIKEY
     */
    private String apiKey;

    /**
     * OpenAi APIHOST
     * 非必填，默认值为 https://api.openai.com/
     */
    private String apiHost;

    /**
     * http代理Host
     * 非必填，用于设置请求OPENAI接口时的HTTP代理host
     */
    private String httpProxyHost;

    /**
     * http代理Port
     * 非必填，用于设置请求OPENAI接口时的HTTP代理port
     */
    private String httpProxyPort;

    /**
     * AI来源
     * @see AiSqlSourceEnum
     */
    private String aiSqlSource;

    /**
     * 自定义AI接口
     * 选择自定义AI时必填，用于设置自定义AI的REST接口URL
     */
    private String restAiUrl;

    /**
     * Rest接口是否流式输出
     * 非必填，默认值为TRUE
     */
    private Boolean restAiStream = Boolean.TRUE;
}
