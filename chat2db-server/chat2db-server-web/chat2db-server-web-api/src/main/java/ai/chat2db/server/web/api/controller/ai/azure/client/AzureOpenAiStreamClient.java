package ai.chat2db.server.web.api.controller.ai.azure.client;

import java.util.List;
import java.util.Objects;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.IterableStream;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义AI接口client
 *
 * @author moji
 */
@Slf4j
public class AzureOpenAiStreamClient {

    /**
     * client
     */
    private OpenAIClient client;

    /**
     * 构造实例对象
     *
     * @param apiKey
     * @param endpoint
     */
    public AzureOpenAiStreamClient(String apiKey, String endpoint) {
        this.client = new OpenAIClientBuilder()
            .credential(new AzureKeyCredential(apiKey))
            .endpoint(endpoint)
            .buildClient();
    }

    /**
     * 问答接口 stream 形式
     *
     * @param deployId
     * @param chatMessages
     * @param eventSourceListener
     */
    public void streamCompletions(String deployId, List<ChatMessage> chatMessages, EventSourceListener eventSourceListener) {
        if (CollectionUtils.isEmpty(chatMessages)) {
            log.error("参数异常：Azure Prompt不能为空");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：AzureEventSourceListener不能为空");
            throw new ParamBusinessException();
        }
        log.info("开始调用Azure Open AI, prompt:{}", chatMessages.get(chatMessages.size() - 1).getContent());
        try {
            IterableStream<ChatCompletions> chatCompletionsStream = client.getChatCompletionsStream(deployId,
                new ChatCompletionsOptions(chatMessages));

            chatCompletionsStream.forEach(chatCompletions -> {
                String text = "";
                System.out.printf("Model ID=%s is created at %d.%n", chatCompletions.getId(),
                    chatCompletions.getCreated());
                for (ChatChoice choice : chatCompletions.getChoices()) {
                    ChatMessage message = choice.getDelta();
                    if (message != null) {
                        log.info("Index: {}, Chat Role: {}.%n", choice.getIndex(), message.getRole());
                        text = message.getContent();
                    }
                }
                Message message = new Message();
                if (StringUtils.isNotBlank(text)) {
                    message.setContent(text);
                    eventSourceListener.onEvent(null, "[DATA]", null, text);
                }
                CompletionsUsage usage = chatCompletions.getUsage();
                if (usage != null) {
                    log.info(
                        "Usage: number of prompt token is {}, number of completion token is {}, and number of total "
                            + "tokens in request and response is {}.%n", usage.getPromptTokens(),
                        usage.getCompletionTokens(), usage.getTotalTokens());
                }
            });
            eventSourceListener.onEvent(null, "[DONE]", null, "[DONE]");
            log.info("结束调用非流式输出自定义AI");
        } catch (Exception e) {
            log.error("请求参数解析异常", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }

}
