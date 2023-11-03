package ai.chat2db.server.web.api.controller.ai.openai.listener;

import java.util.Objects;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.controller.ai.baichuan.model.BaichuanChatCompletions;
import ai.chat2db.server.web.api.controller.ai.baichuan.model.BaichuanChatMessage;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import ai.chat2db.server.web.api.controller.ai.response.ChatCompletionResponse;

import ai.chat2db.server.web.api.controller.ai.zhipu.model.ZhipuChatCompletions;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
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
 * 描述：OpenAIEventSourceListener
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
        log.info("OpenAI返回数据：{}", data);
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]")
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        DataResult<Config> chat2dbModel = configService.find(Chat2dbAIClient.CHAT2DB_OPENAI_MODEL);
        String model = Objects.nonNull(chat2dbModel.getData()) ? chat2dbModel.getData().getContent() : AiSqlSourceEnum.OPENAI.getCode();
        AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(model);
        String text = "";
        String completionId = null;
        // 读取Json
        switch (aiSqlSourceEnum) {
            case BAICHUANAI:
                BaichuanChatCompletions chatCompletions = mapper.readValue(data, BaichuanChatCompletions.class);
                for (BaichuanChatMessage message : chatCompletions.getData().getMessages()) {
                    if (message != null) {
                        if (message.getContent() != null) {
                            text = message.getContent();
                        }
                    }
                }
                break;
            case ZHIPUAI:
                ZhipuChatCompletions zhipuChatCompletions = mapper.readValue(data, ZhipuChatCompletions.class);
                text = zhipuChatCompletions.getData();
                if (Objects.isNull(text)) {
                    for (FastChatMessage message : zhipuChatCompletions.getBody().getChoices()) {
                        if (message != null && message.getContent() != null) {
                            text = message.getContent();
                        }
                    }
                }
                break;
            default:
                ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
                text = completionResponse.getChoices().get(0).getDelta() == null
                        ? completionResponse.getChoices().get(0).getText()
                        : completionResponse.getChoices().get(0).getDelta().getContent();
                completionId = completionResponse.getId();
                break;
        }

        Message message = new Message();
        if (text != null) {
            message.setContent(text);
            sseEmitter.send(SseEmitter.event()
                .id(completionId)
                .data(message)
                .reconnectTime(3000));
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        sseEmitter.complete();
        log.info("OpenAI关闭sse连接...");
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        try {
            if (Objects.isNull(response)) {
                String message = t.getMessage();
                if ("No route to host".equals(message)) {
                    message = "网络连接超时，请百度自行解决网络问题";
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
                log.error("OpenAI  sse连接异常data：{}", bodyString, t);
            } else {
                log.error("OpenAI  sse连接异常data：{}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("出现异常,请在帮助中查看详细日志：" + bodyString);
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
