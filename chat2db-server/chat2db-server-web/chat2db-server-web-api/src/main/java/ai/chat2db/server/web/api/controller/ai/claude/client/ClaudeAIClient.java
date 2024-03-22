
package ai.chat2db.server.web.api.controller.ai.claude.client;

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
public class ClaudeAIClient {

    public static final String CLAUDE_SESSION_KEY = "claude.sessionKey";

    public static final String CLAUDE_API_HOST = "claude.apiHost";

    public static final String CLAUDE_ORG_ID = "claude.orgId";

    public static final String CLAUDE_USER_ID = "claude.userId";


    private static ClaudeAiStreamClient CLAUDE_AI_STREAM_CLIENT;
    private static String apiKey;

    public static ClaudeAiStreamClient getInstance() {
        if (CLAUDE_AI_STREAM_CLIENT != null) {
            return CLAUDE_AI_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static ClaudeAiStreamClient singleton() {
        if (CLAUDE_AI_STREAM_CLIENT == null) {
            synchronized (ClaudeAIClient.class) {
                if (CLAUDE_AI_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return CLAUDE_AI_STREAM_CLIENT;
    }

    public static void refresh() {
        String apikey = "";
        String orgId = "";
        String userId = "";
        String apiHost = "https://claude.ai/api/append_message";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(CLAUDE_API_HOST).getData();
        if (apiHostConfig != null) {
            apiHost = apiHostConfig.getContent();
        }
        Config config = configService.find(CLAUDE_SESSION_KEY).getData();
        if (config != null) {
            apikey = config.getContent();
        }
        Config orgConfig = configService.find(CLAUDE_ORG_ID).getData();
        if (orgConfig != null) {
            orgId = orgConfig.getContent();
        }
        Config userConfig = configService.find(CLAUDE_USER_ID).getData();
        if (userConfig != null) {
            userId = userConfig.getContent();
        }
        log.info("refresh claude sessionKey:{}", maskApiKey(apikey));
        CLAUDE_AI_STREAM_CLIENT = ClaudeAiStreamClient.builder().apiHost(apiHost)
                .sessionKey(apikey).orgId(orgId).userId(userId).build();
        apiKey = apikey;
    }

    private static String maskApiKey(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        StringBuilder maskedString = new StringBuilder(input);
        for (int i = input.length() / 4; i < input.length() / 2; i++) {
            maskedString.setCharAt(i, '*');
        }
        return maskedString.toString();
    }
}
