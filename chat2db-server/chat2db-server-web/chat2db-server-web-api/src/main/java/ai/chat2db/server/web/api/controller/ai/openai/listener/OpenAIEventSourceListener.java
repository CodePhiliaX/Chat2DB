package ai.chat2db.server.web.api.controller.ai.openai.listener;

import java.util.Objects;

import ai.chat2db.server.web.api.controller.ai.response.ChatCompletionResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * description：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class OpenAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    public OpenAIEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI returns data: {}", data);
        if (data.equals("[DONE]")) {
            log.info("OpenAI returns data ended");
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]")
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Read JSON
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        String text = completionResponse.getChoices().get(0).getDelta() == null
            ? completionResponse.getChoices().get(0).getText()
            : completionResponse.getChoices().get(0).getDelta().getContent();
        Message message = new Message();
        if (text != null) {
            message.setContent(text);
            sseEmitter.send(SseEmitter.event()
                .id(completionResponse.getId())
                .data(message)
                .reconnectTime(3000));
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        sseEmitter.complete();
        log.info("OpenAI closes sse connection...");
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        try {
            if (Objects.isNull(response)) {
                String message = t.getMessage();
                if ("No route to host".equals(message)) {
                    message = "The network connection timed out. Please Baidu solve the network problem by yourself.";
                }
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
            String bodyString = null;
            if (Objects.nonNull(body)) {
                bodyString = body.string();
                log.error("OpenAI sse connection exception data: {}", bodyString, t);
            } else {
                log.error("OpenAI sse connection exception data: {}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("An exception occurred, please view the detailed log in the help：" + bodyString);
            sseEmitter.send(SseEmitter.event()
                .id("[ERROR]")
                .data(message));
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]"));
            sseEmitter.complete();
        } catch (Exception exception) {
            log.error("Exception in sending data:", exception);
        }
    }
}
