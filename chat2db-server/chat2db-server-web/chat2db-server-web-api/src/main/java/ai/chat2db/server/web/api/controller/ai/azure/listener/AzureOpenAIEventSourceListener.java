package ai.chat2db.server.web.api.controller.ai.azure.listener;

import java.io.IOException;
import java.util.Objects;

import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatChoice;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatCompletions;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatMessage;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureCompletionsUsage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class AzureOpenAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    private ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public AzureOpenAIEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("AzureOpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("AzureOpenAI返回数据：{}", data);
        if (data.equals("[DONE]")) {
            log.info("AzureOpenAI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]")
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }

        AzureChatCompletions chatCompletions = mapper.readValue(data, AzureChatCompletions.class);
        String text = "";
        log.info("Model ID={} is created at {}.", chatCompletions.getId(),
            chatCompletions.getCreated());
        for (AzureChatChoice choice : chatCompletions.getChoices()) {
            AzureChatMessage message = choice.getDelta();
            if (message != null) {
                log.info("Index: {}, Chat Role: {}", choice.getIndex(), message.getRole());
                if (message.getContent() != null) {
                    text = message.getContent();
                }
            }
        }

        AzureCompletionsUsage usage = chatCompletions.getUsage();
        if (usage != null) {
            log.info(
                "Usage: number of prompt token is {}, number of completion token is {}, and number of total "
                    + "tokens in request and response is {}.%n", usage.getPromptTokens(),
                usage.getCompletionTokens(), usage.getTotalTokens());
        }

        Message message = new Message();
        message.setContent(text);
        sseEmitter.send(SseEmitter.event()
            .id(null)
            .data(message)
            .reconnectTime(3000));
    }

    @Override
    public void onClosed(EventSource eventSource) {
        try {
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sseEmitter.complete();
        log.info("AzureOpenAI close sse connection...");
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
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
                log.error("Azure OpenAI sse response：{}", bodyString);
            } else {
                log.error("Azure OpenAI sse response：{}，error：{}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("Azure OpenAI error：" + bodyString);
            sseEmitter.send(SseEmitter.event()
                .id("[ERROR]")
                .data(message));
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]"));
            sseEmitter.complete();
        } catch (Exception exception) {
            log.error("Azure OpenAI发送数据异常:", exception);
        }
    }
}
