
package ai.chat2db.server.web.api.controller.ai.wenxin.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.controller.ai.fastchat.client.FastChatAIStreamClient;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author moji
 * @date 23/09/26
 */
@Slf4j
public class WenxinAIClient {

    /**
     * WENXIN_ACCESS_TOKEN
     */
    public static final String WENXIN_ACCESS_TOKEN = "wenxin.access.token";

    /**
     * WENXIN_HOST
     */
    public static final String WENXIN_HOST = "wenxin.host";

    /**
     * WENXIN_MODEL
     */
    public static final String WENXIN_MODEL= "wenxin.model";

    /**
     * Wenxin embedding model
     */
    public static final String WENXIN_EMBEDDING_MODEL = "wenxin.embedding.model";

    private static WenxinAIStreamClient WENXIN_AI_CLIENT;


    public static WenxinAIStreamClient getInstance() {
        if (WENXIN_AI_CLIENT != null) {
            return WENXIN_AI_CLIENT;
        } else {
            return singleton();
        }
    }

    private static WenxinAIStreamClient singleton() {
        if (WENXIN_AI_CLIENT == null) {
            synchronized (WenxinAIClient.class) {
                if (WENXIN_AI_CLIENT == null) {
                    refresh();
                }
            }
        }
        return WENXIN_AI_CLIENT;
    }

    public static void refresh() {
        String apiHost = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro";
        String accessToken = "";
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(WENXIN_HOST).getData();
        if (apiHostConfig != null && StringUtils.isNotBlank(apiHostConfig.getContent())) {
            apiHost = apiHostConfig.getContent();
            if (apiHost.endsWith("/")) {
                apiHost = apiHost.substring(0, apiHost.length() - 1);
            }
        }
        Config config = configService.find(WENXIN_ACCESS_TOKEN).getData();
        if (config != null && StringUtils.isNotBlank(config.getContent())) {
            accessToken = config.getContent();
        }
        WENXIN_AI_CLIENT = WenxinAIStreamClient.builder().accessToken(accessToken).apiHost(apiHost).build();
    }

}
