package ai.chat2db.server.domain.api.model;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import lombok.Data;

/**
 * @author moji
 * @version ChatGptConfig.java, v 0.1 2023年05月09日 13:47 moji Exp $
 * @date 2023/05/09
 */
@Data
public class ChatGptConfig {
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
     * @see AiSqlSourceEnum
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
