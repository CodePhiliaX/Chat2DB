
package ai.chat2db.server.web.api.controller.ai.azure.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : OpenAIClient.java
 */
@Slf4j
public class AzureOpenAIClient {

    /**
     * AZURE OPENAI KEY
     */
    public static final String AZURE_CHATGPT_API_KEY = "azure.chatgpt.apiKey";

    /**
     * AZURE OPENAI ENDPOINT
     */
    public static final String AZURE_CHATGPT_ENDPOINT = "azure.chatgpt.endpoint";

    /**
     * AZURE OPENAI DEPLOYMENT ID
     */
    public static final String AZURE_CHATGPT_DEPLOYMENT_ID = "azure.chatgpt.deployment.id";

    private static AzureOpenAiStreamClient OPEN_AI_CLIENT;
    private static String apiKey;

    public static AzureOpenAiStreamClient getInstance() {
        if (OPEN_AI_CLIENT != null) {
            return OPEN_AI_CLIENT;
        } else {
            return singleton();
        }
    }

    private static AzureOpenAiStreamClient singleton() {
        if (OPEN_AI_CLIENT == null) {
            synchronized (AzureOpenAIClient.class) {
                if (OPEN_AI_CLIENT == null) {
                    refresh();
                }
            }
        }
        return OPEN_AI_CLIENT;
    }

    public static void refresh() {
        String key = "";
        String apiEndpoint = "";
        String deployId = "gpt-3.5-turbo";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(AZURE_CHATGPT_ENDPOINT).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiEndpoint = apiHostConfig.getContent();
        }
        Config config = configService.find(AZURE_CHATGPT_API_KEY).getData();
        if (config != null && StringUtils.isNotBlank(config.getContent())) {
            key = config.getContent();
        }
        Config deployConfig = configService.find(AZURE_CHATGPT_DEPLOYMENT_ID).getData();
        if (deployConfig != null && StringUtils.isNotBlank(deployConfig.getContent())) {
            deployId = deployConfig.getContent();
        }
        log.info("refresh azure openai apikey:{}", maskApiKey(key));
        OPEN_AI_CLIENT = AzureOpenAiStreamClient.builder().apiKey(key).endpoint(apiEndpoint).deployId(deployId)
            .build();
        apiKey = key;
    }

    private static String maskApiKey(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder maskedString = new StringBuilder(input);
        for (int i = input.length() / 2; i < input.length() / 2; i++) {
            maskedString.setCharAt(i, '*');
        }
        return maskedString.toString();
    }

}
