
package ai.chat2db.server.web.api.controller.config.request;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SystemConfigRequest.java
 */
@Data
public class AISystemConfigRequest {

    /**
     * chat2db APIKEY
     */
    private String chat2dbApiKey;

    /**
     * chat2db APIHOST
     */
    private String chat2dbApiHost;

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

    /**
     * Get Azure OpenAI key credential from the Azure Portal
     */
    private String azureApiKey;

    /**
     * Get Azure OpenAI endpoint from the Azure Portal
     */
    private String azureEndpoint;

    /**
     * deploymentId of the deployed model, default gpt-3.5-turbo
     */
    private String azureDeploymentId;
}
