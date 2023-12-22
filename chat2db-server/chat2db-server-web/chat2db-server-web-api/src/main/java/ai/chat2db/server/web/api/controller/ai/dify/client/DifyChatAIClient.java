package ai.chat2db.server.web.api.controller.ai.dify.client;

import ai.chat2db.server.web.api.controller.ai.azure.client.AzureOpenAiStreamClient;
import ai.chat2db.server.web.api.controller.ai.dify.listener.DifyChatAIEventSourceListener;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;

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


    public static void refresh() {

    }

    public static DifyChatAIClient getInstance() {
        return null;
    }

    public void streamCompletions(List<Message> messages, DifyChatAIEventSourceListener eventSourceListener) {

    }
}
