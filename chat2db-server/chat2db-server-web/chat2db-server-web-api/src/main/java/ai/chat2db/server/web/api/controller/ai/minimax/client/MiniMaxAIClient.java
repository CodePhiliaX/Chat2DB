package ai.chat2db.server.web.api.controller.ai.minimax.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * MiniMax AI client
 *
 * @author octo-patch
 */
@Slf4j
public class MiniMaxAIClient {

    /**
     * MiniMax AI API Key
     */
    public static final String MINIMAX_API_KEY = "minimax.ai.apiKey";

    /**
     * MiniMax AI API Host
     */
    public static final String MINIMAX_HOST = "minimax.ai.host";

    /**
     * MiniMax AI Model
     */
    public static final String MINIMAX_MODEL = "minimax.ai.model";

    private static MiniMaxAIStreamClient MINIMAX_AI_STREAM_CLIENT;

    public static MiniMaxAIStreamClient getInstance() {
        if (MINIMAX_AI_STREAM_CLIENT != null) {
            return MINIMAX_AI_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static MiniMaxAIStreamClient singleton() {
        if (MINIMAX_AI_STREAM_CLIENT == null) {
            synchronized (MiniMaxAIClient.class) {
                if (MINIMAX_AI_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return MINIMAX_AI_STREAM_CLIENT;
    }

    /**
     * Refresh client
     */
    public static void refresh() {
        String apiUrl = "";
        String apiKey = "";
        String model = "";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(MINIMAX_HOST).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiUrl = apiHostConfig.getContent();
        }
        Config config = configService.find(MINIMAX_API_KEY).getData();
        if (config != null) {
            apiKey = config.getContent();
        }
        Config modelConfig = configService.find(MINIMAX_MODEL).getData();
        if (modelConfig != null && StringUtils.isNotBlank(modelConfig.getContent())) {
            model = modelConfig.getContent();
        }
        MINIMAX_AI_STREAM_CLIENT = MiniMaxAIStreamClient.builder().apiKey(apiKey).apiHost(apiUrl).model(model)
                .build();
    }

}
