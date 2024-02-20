package ai.chat2db.server.web.api.controller.ai.zhipu.listener;

import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import ai.chat2db.server.web.api.controller.ai.openai.listener.OpenAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.utils.PromptService;
import ai.chat2db.server.web.api.controller.ai.zhipu.client.ZhipuChatAIClient;
import ai.chat2db.server.web.api.controller.ai.zhipu.model.ZhipuChatCompletionsOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class ZhipuChatAIEventSourceListener extends OpenAIEventSourceListener {
    
    public ZhipuChatAIEventSourceListener(SseEmitter sseEmitter, PromptService promptService,
            ChatQueryRequest queryRequest, LoginUser loginUser) {
        super(sseEmitter, promptService, queryRequest, loginUser);
    }


    @Override
    public void functionCall(String prompt){
        Long uid = loginUser.getId();
        List<FastChatMessage> messages = promptService.getFastChatMessage(Objects.toString(uid), prompt);
        String requestId = String.valueOf(System.currentTimeMillis());
        ZhipuChatCompletionsOptions completionsOptions = ZhipuChatCompletionsOptions.builder()
                .requestId(requestId)
                .stream(true)
                .toolChoice("auto")
                .messages(messages)
                .build();
        ZhipuChatAIClient.getInstance().streamCompletions(completionsOptions, this);
    }
}
