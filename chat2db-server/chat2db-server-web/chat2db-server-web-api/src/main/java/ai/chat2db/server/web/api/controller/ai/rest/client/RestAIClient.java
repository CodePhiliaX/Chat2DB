
package ai.chat2db.server.web.api.controller.ai.rest.client;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author moji
 * @version : RestAIClient.java
 */
@Slf4j
public class RestAIClient {

    /**
     * Interface source selected by AI SQL
     */
    public static final String AI_SQL_SOURCE = "ai.sql.source";

    /**
     * Customized AI interface address
     */
    public static final String REST_AI_URL = "rest.ai.url";

    /**
     * Custom AI interface request method
     */
    public static final String REST_AI_STREAM_OUT = "rest.ai.stream";

    private static RestAiStreamClient REST_AI_STREAM_CLIENT;

    public static RestAiStreamClient getInstance() {
        if (REST_AI_STREAM_CLIENT != null) {
            return REST_AI_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static RestAiStreamClient singleton() {
        if (REST_AI_STREAM_CLIENT == null) {
            synchronized (RestAIClient.class) {
                if (REST_AI_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return REST_AI_STREAM_CLIENT;
    }

    /**
     * Refresh client
     */
    public static void refresh() {
        String apiUrl = "";
        Boolean stream = Boolean.TRUE;
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config apiHostConfig = configService.find(REST_AI_URL).getData();
        if (apiHostConfig != null) {
            apiUrl = apiHostConfig.getContent();
        }
        Config config = configService.find(REST_AI_STREAM_OUT).getData();
        if (config != null) {
            stream = Boolean.valueOf(config.getContent());
        }
        REST_AI_STREAM_CLIENT = new RestAiStreamClient(apiUrl, stream);
    }

}
