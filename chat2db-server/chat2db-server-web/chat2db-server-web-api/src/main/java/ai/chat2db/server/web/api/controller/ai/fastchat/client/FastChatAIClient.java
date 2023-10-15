
package ai.chat2db.server.web.api.controller.ai.fastchat.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author moji
 * @date 23/09/26
 */
@Slf4j
public class FastChatAIClient {

    /**
     * FASTCHAT OPENAI KEY
     */
    public static final String FASTCHAT_API_KEY = "fastchat.chatgpt.apiKey";

    /**
     * FASTCHAT OPENAI HOST
     */
    public static final String FASTCHAT_HOST = "fastchat.host";

    /**
     * FASTCHAT OPENAI model
     */
    public static final String FASTCHAT_MODEL= "fastchat.model";

    /**
     * FASTCHAT OPENAI embedding model
     */
    public static final String FASTCHAT_EMBEDDING_MODEL = "fastchat.embedding.model";

    private static FastChatAIStreamClient FASTCHAT_AI_CLIENT;


    public static FastChatAIStreamClient getInstance() {
        if (FASTCHAT_AI_CLIENT != null) {
            return FASTCHAT_AI_CLIENT;
        } else {
            return singleton();
        }
    }

    private static FastChatAIStreamClient singleton() {
        if (FASTCHAT_AI_CLIENT == null) {
            synchronized (FastChatAIClient.class) {
                if (FASTCHAT_AI_CLIENT == null) {
                    refresh();
                }
            }
        }
        return FASTCHAT_AI_CLIENT;
    }

    public static void refresh() {
        String apiKey = "";
        String apiHost = "";
        String model = "";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(FASTCHAT_HOST).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiHost = apiHostConfig.getContent();
        }
        Config config = configService.find(FASTCHAT_API_KEY).getData();
        if (config != null && StringUtils.isNotBlank(config.getContent())) {
            apiKey = config.getContent();
        }
        Config deployConfig = configService.find(FASTCHAT_MODEL).getData();
        if (deployConfig != null && StringUtils.isNotBlank(deployConfig.getContent())) {
            model = deployConfig.getContent();
        }
        FASTCHAT_AI_CLIENT = FastChatAIStreamClient.builder().apiKey(apiKey).apiHost(apiHost).model(model)
            .build();
    }

}
