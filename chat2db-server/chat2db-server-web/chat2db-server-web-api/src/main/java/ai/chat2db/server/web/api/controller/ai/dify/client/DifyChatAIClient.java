package ai.chat2db.server.web.api.controller.ai.dify.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.controller.ai.azure.client.AzureOpenAiStreamClient;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2DBAIStreamClient;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.ai.dify.listener.DifyChatAIEventSourceListener;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import com.unfbx.chatgpt.constant.OpenAIConst;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class DifyChatAIClient {

    /**
     * ZHIPU OPENAI KEY
     */
    public static final String DIFYCHAT_API_KEY = "difychat.apiKey";

    /**
     * ZHIPU OPENAI HOST
     */
    public static final String DIFYCHAT_HOST = "difychat.host";


    private static DifyChatAiStreamClient DIFY_CHAT_STREAM_CLIENT;

    public static DifyChatAiStreamClient getInstance() {
        if (DIFY_CHAT_STREAM_CLIENT != null) {
            return DIFY_CHAT_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static DifyChatAiStreamClient singleton() {
        if (DIFY_CHAT_STREAM_CLIENT == null) {
            synchronized (DifyChatAIClient.class) {
                if (DIFY_CHAT_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return DIFY_CHAT_STREAM_CLIENT;
    }


    public static void refresh() {

        String apikey;
        String apiHost = ApplicationContextUtil.getProperty(DIFYCHAT_HOST);
        if (StringUtils.isBlank(apiHost)) {
            apiHost = OpenAIConst.OPENAI_HOST;
        }
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(DIFYCHAT_HOST).getData();
        if (apiHostConfig != null) {
            apiHost = apiHostConfig.getContent();
        }
        Config config = configService.find(DIFYCHAT_API_KEY).getData();
        if (config != null) {
            apikey = config.getContent();
        } else {
            apikey = ApplicationContextUtil.getProperty(DIFYCHAT_API_KEY);
        }

        log.info("refresh dify chat apiHost:{} apikey:{}", apiHost, maskApiKey(apikey));
        DIFY_CHAT_STREAM_CLIENT = DifyChatAiStreamClient.builder().apiHost(apiHost).apiKey(apikey).build();

    }

    private static String maskApiKey(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder maskedString = new StringBuilder(input);
        for (int i = input.length() / 4; i < input.length() / 2; i++) {
            maskedString.setCharAt(i, '*');
        }
        return maskedString.toString();
    }


}
