package ai.chat2db.server.domain.api.model;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import lombok.Data;

/**
 * @author moji
 * @version ChatGptConfig.java, v 0.1 May 9, 2023 13:47 moji Exp $
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
     * HTTP proxy host
     */
    private String httpProxyHost;

    /**
     * HTTP proxy Port
     */
    private String httpProxyPort;

    /**
     * AI type
     * @see AiSqlSourceEnum
     */
    private String aiSqlSource;

    /**
     * Custom AI interface
     */
    private String restAiUrl;

    /**
     * Whether the Rest interface streams output
     * Optional, default value is TRUE
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
