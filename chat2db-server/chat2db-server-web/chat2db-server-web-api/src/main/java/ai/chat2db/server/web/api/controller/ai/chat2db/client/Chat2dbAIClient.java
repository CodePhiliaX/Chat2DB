
package ai.chat2db.server.web.api.controller.ai.chat2db.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import com.google.common.collect.Lists;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.constant.OpenAIConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : OpenAIClient.java
 */
@Slf4j
public class Chat2dbAIClient {

    public static final String CHAT2DB_OPENAI_KEY = "chat2db.apiKey";

    /**
     * OPENAI接口域名
     */
    public static final String CHAT2DB_OPENAI_HOST = "chat2db.apiHost";


    private static OpenAiStreamClient CHAT2DB_AI_STREAM_CLIENT;
    private static String apiKey;

    public static OpenAiStreamClient getInstance() {
        if (CHAT2DB_AI_STREAM_CLIENT != null) {
            return CHAT2DB_AI_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static OpenAiStreamClient singleton() {
        if (CHAT2DB_AI_STREAM_CLIENT == null) {
            synchronized (Chat2dbAIClient.class) {
                if (CHAT2DB_AI_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return CHAT2DB_AI_STREAM_CLIENT;
    }

    public static void refresh() {
        String apikey;
        String apiHost = ApplicationContextUtil.getProperty(CHAT2DB_OPENAI_HOST);
        if (StringUtils.isBlank(apiHost)) {
            apiHost = OpenAIConst.OPENAI_HOST;
        }
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(CHAT2DB_OPENAI_HOST).getData();
        if (apiHostConfig != null) {
            apiHost = apiHostConfig.getContent();
        }
        Config config = configService.find(CHAT2DB_OPENAI_KEY).getData();
        if (config != null) {
            apikey = config.getContent();
        } else {
            apikey = ApplicationContextUtil.getProperty(CHAT2DB_OPENAI_KEY);
        }
        log.info("refresh chat2db apikey:{}", maskApiKey(apikey));
        CHAT2DB_AI_STREAM_CLIENT = OpenAiStreamClient.builder().apiHost(apiHost).apiKey(
            Lists.newArrayList(apikey)).build();
        apiKey = apikey;
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
