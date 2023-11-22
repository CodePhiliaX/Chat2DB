
package ai.chat2db.server.web.api.controller.ai.baichuan.client;

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
public class BaichuanAIClient {

    /**
     * BAICHUAN OPENAI KEY
     */
    public static final String BAICHUAN_API_KEY = "baichuan.chatgpt.apiKey";

    /**
     * BAICHUAN OPENAI SECRET KEY
     */
    public static final String BAICHUAN_SECRET_KEY = "baichuan.chatgpt.secretKey";

    /**
     * BAICHUAN OPENAI HOST
     */
    public static final String BAICHUAN_HOST = "baichuan.host";

    /**
     * BAICHUAN OPENAI model
     */
    public static final String BAICHUAN_MODEL= "baichuan.model";

    /**
     * BAICHUAN OPENAI embedding model
     */
    public static final String BAICHUAN_EMBEDDING_MODEL = "baichuan.embedding.model";

    private static BaichuanAIStreamClient BAICHUAN_AI_CLIENT;


    public static BaichuanAIStreamClient getInstance() {
        if (BAICHUAN_AI_CLIENT != null) {
            return BAICHUAN_AI_CLIENT;
        } else {
            return singleton();
        }
    }

    private static BaichuanAIStreamClient singleton() {
        if (BAICHUAN_AI_CLIENT == null) {
            synchronized (BaichuanAIClient.class) {
                if (BAICHUAN_AI_CLIENT == null) {
                    refresh();
                }
            }
        }
        return BAICHUAN_AI_CLIENT;
    }

    public static void refresh() {
        String apiKey = "";
        String apiHost = "https://api.baichuan-ai.com/v1/stream/chat";
        String model = "Baichuan2-53B";
        String secretKey = "";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(BAICHUAN_HOST).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiHost = apiHostConfig.getContent();
            if (apiHost.endsWith("/")) {
                apiHost = apiHost.substring(0, apiHost.length() - 1);
            }
        }
        Config config = configService.find(BAICHUAN_API_KEY).getData();
        if (config != null && StringUtils.isNotBlank(config.getContent())) {
            apiKey = config.getContent();
        }
        Config secretConfig = configService.find(BAICHUAN_SECRET_KEY).getData();
        if (secretConfig != null && StringUtils.isNotBlank(secretConfig.getContent())) {
            secretKey = secretConfig.getContent();
        }
        Config deployConfig = configService.find(BAICHUAN_MODEL).getData();
        if (deployConfig != null && StringUtils.isNotBlank(deployConfig.getContent())) {
            model = deployConfig.getContent();
        }
        BAICHUAN_AI_CLIENT = BaichuanAIStreamClient.builder().apiKey(apiKey).secretKey(secretKey)
                .apiHost(apiHost).model(model).build();
    }

}
