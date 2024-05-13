
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
     * Required when using the OpenAi interface, you can go to the OpenAI official website to view APIKEY
     */
    private String apiKey;

    /**
     * OpenAi APIHOST
     * Optional, the default value is https://api.openai.com/
     */
    private String apiHost;

    /**
     * http proxy Host
     * Optional, used to set the HTTP proxy host when requesting the OPENAI interface
     */
    private String httpProxyHost;

    /**
     * http proxy Port
     * Optional, used to set the HTTP proxy port when requesting the OPENAI interface
     */
    private String httpProxyPort;

    /**
     * AI source
     * @see AiSqlSourceEnum
     */
    private String aiSqlSource;

    /**
     * Customized AI interface
     * Required when selecting custom AI, used to set the REST interface URL of the custom AI
     */
    private String restAiUrl;

    /**
     * Whether the Rest interface has streaming output
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
