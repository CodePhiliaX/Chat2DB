package ai.chat2db.server.web.api.controller.ai.dify.client;

import ai.chat2db.server.web.api.controller.ai.dify.listener.DifyChatAIEventSourceListener;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DifyChatAiStreamClient {

    /**
     * apikey
     */
    @Getter
    @NotNull
    private String apiKey;

    /**
     * apiHost
     */
    @Getter
    @NotNull
    private String apiHost;


    public void streamCompletions(List<Message> messages, DifyChatAIEventSourceListener eventSourceListener) {

    }
}
