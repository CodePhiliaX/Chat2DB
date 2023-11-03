
package ai.chat2db.server.web.api.controller.ai.tongyi.client;

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
public class TongyiChatAIClient {

    /**
     * TONGYI OPENAI KEY
     */
    public static final String TONGYI_API_KEY = "tongyi.chatgpt.apiKey";

    /**
     * TONGYI OPENAI HOST
     */
    public static final String TONGYI_HOST = "tongyi.host";

    /**
     * TONGYI OPENAI model
     */
    public static final String TONGYI_MODEL= "tongyi.model";

    /**
     * TONGYI OPENAI embedding model
     */
    public static final String TONGYI_EMBEDDING_MODEL = "tongyi.embedding.model";

    private static TongyiChatAIStreamClient TONGYI_AI_CLIENT;


    public static TongyiChatAIStreamClient getInstance() {
        if (TONGYI_AI_CLIENT != null) {
            return TONGYI_AI_CLIENT;
        } else {
            return singleton();
        }
    }

    private static TongyiChatAIStreamClient singleton() {
        if (TONGYI_AI_CLIENT == null) {
            synchronized (TongyiChatAIClient.class) {
                if (TONGYI_AI_CLIENT == null) {
                    refresh();
                }
            }
        }
        return TONGYI_AI_CLIENT;
    }

    public static void refresh() {
        String apiKey = "";
        String apiHost = "";
        String model = "";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(TONGYI_HOST).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiHost = apiHostConfig.getContent();
        }
        Config config = configService.find(TONGYI_API_KEY).getData();
        if (config != null && StringUtils.isNotBlank(config.getContent())) {
            apiKey = config.getContent();
        }
        Config deployConfig = configService.find(TONGYI_MODEL).getData();
        if (deployConfig != null && StringUtils.isNotBlank(deployConfig.getContent())) {
            model = deployConfig.getContent();
        }
        TONGYI_AI_CLIENT = TongyiChatAIStreamClient.builder().apiKey(apiKey).apiHost(apiHost).model(model)
            .build();
    }

}
