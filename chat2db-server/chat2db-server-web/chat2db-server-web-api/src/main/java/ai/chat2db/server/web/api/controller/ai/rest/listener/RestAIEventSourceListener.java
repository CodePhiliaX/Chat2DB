package ai.chat2db.server.web.api.controller.ai.rest.listener;

import java.util.Objects;

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
 * 描述：RESTAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class RestAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    public RestAIEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("REST AI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("REST AI返回数据：{}", data);
        String end = "[DONE]";
        if (data.equals(end)) {
            log.info("REST AI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                .id(end)
                .data(end)
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }
        Message message = new Message();
        if (StringUtils.isNotBlank(data)) {
            data = data.replaceAll("^\"|\"$", "");
            data = data.replaceAll("\\\\n", "\n");
            message.setContent(data);
            sseEmitter.send(SseEmitter.event()
                .id(id)
                .data(message)
                .reconnectTime(3000));
        }
    }

    @SneakyThrows
    @Override
    public void onClosed(EventSource eventSource) {
        log.info("REST AI关闭sse连接...");
        sseEmitter.send(SseEmitter.event()
            .id("[DONE]")
            .data("[DONE]")
            .reconnectTime(3000));
        sseEmitter.complete();
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
                log.error("REST AI sse body error：{}，exception：{}", bodyString, t);
            } else {
                log.error("REST AI sse response error：{}，exception：{}", response, t);
            }
            if (Objects.nonNull(eventSource)) {
                eventSource.cancel();
            }
            Message message = new Message();
            message.setContent("Rest AI Error:" + bodyString);
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
