package ai.chat2db.server.web.api.controller.ai.claude.listener;

import ai.chat2db.server.web.api.controller.ai.claude.model.ClaudeCompletionResponse;
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

import java.util.Objects;

/**
 * ClaudeAIEventSourceListener
 */
@Slf4j
public class ClaudeAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    private ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public ClaudeAIEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("ClaudeAIEventSourceListener...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("Claude AI data：{}", data);
        if (data.equals("[DONE]")) {
            log.info("Claude AI end");
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]")
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }
        // 读取Json
        ClaudeCompletionResponse completionResponse = mapper.readValue(data, ClaudeCompletionResponse.class);
        String text = completionResponse.getCompletion();
        Message message = new Message();
        if (text != null) {
            message.setContent(text);
            sseEmitter.send(SseEmitter.event()
                .id(null)
                .data(message)
                .reconnectTime(3000));
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        sseEmitter.complete();
        log.info("Claude AI closed...");
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
            String bodyString = null;
            if (Objects.nonNull(body)) {
                bodyString = body.string();
                log.error("Claude sse error：{}", bodyString, t);
            } else {
                log.error("Claude sse body error：{}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("Claude sse error：" + bodyString);
            sseEmitter.send(SseEmitter.event()
                .id("[ERROR]")
                .data(message));
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]"));
            sseEmitter.complete();
        } catch (Exception exception) {
            log.error("发送数据异常:", exception);
        }
    }
}
