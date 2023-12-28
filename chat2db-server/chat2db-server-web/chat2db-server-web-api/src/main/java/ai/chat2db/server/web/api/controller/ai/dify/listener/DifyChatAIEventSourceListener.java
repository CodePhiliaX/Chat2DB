package ai.chat2db.server.web.api.controller.ai.dify.listener;


import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatChoice;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatCompletions;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatMessage;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureCompletionsUsage;
import ai.chat2db.server.web.api.controller.ai.config.LocalCache;
import ai.chat2db.server.web.api.controller.ai.dify.model.DifyChatConstant;
import ai.chat2db.server.web.api.controller.ai.dify.model.DifyChatStreamEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class DifyChatAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    private String uid;

    private ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


    public DifyChatAIEventSourceListener(SseEmitter sseEmitter, String uid) {
        this.sseEmitter = sseEmitter;
        this.uid = uid;
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sseEmitter.complete();
        log.info("DifyChatAI close sse connection...");
    }

    @Override
    @SneakyThrows
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        log.info("DifyChatAI：{}", data);
        DifyChatStreamEvent event = mapper.readValue(data, DifyChatStreamEvent.class);
        if (DifyChatConstant.EVENT_END_TAG.equals(event.getEvent())) {
            log.info("DifyChatAI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            return;
        }

        String text = event.getAnswer();
        LocalCache.CACHE.put(DifyChatConstant.CONVERSATION_CACHE_PREFIX + uid, event.getConversationId());
        log.info("Model ID={} is created at {}.", event.getId(), event.getCreatedAt());
        Message message = new Message();
        message.setContent(text);
        sseEmitter.send(SseEmitter.event()
                .id(null)
                .data(message)
                .reconnectTime(3000));

    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        try {
            if (Objects.isNull(response)) {
                String message = t.getMessage();
                Message sseMessage = new Message();
                sseMessage.setContent(message);
                sseEmitter.send(SseEmitter.event()
                        .id("[ERROR]")
                        .data(sseMessage));
                sseEmitter.send(SseEmitter.event()
                        .id("[DONE]")
                        .data("[DONE]"));
                sseEmitter.complete();
                return;
            }
            ResponseBody body = response.body();
            String bodyString = Objects.nonNull(t) ? t.getMessage() : "";
            if (Objects.nonNull(body)) {
                bodyString = body.string();
                if (StringUtils.isBlank(bodyString) && Objects.nonNull(t)) {
                    bodyString = t.getMessage();
                }
                log.error("DifyChatAI sse response：{}", bodyString);
            } else {
                log.error("DifyChatAI sse response：{}，error：{}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("DifyChatAI error：" + bodyString);
            sseEmitter.send(SseEmitter.event()
                    .id("[ERROR]")
                    .data(message));
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]"));
            sseEmitter.complete();
        } catch (Exception exception) {
            log.error("DifyChatAI 发送数据异常:", exception);
        }
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.info("DifyChatAI 建立sse连接...");
    }
}
