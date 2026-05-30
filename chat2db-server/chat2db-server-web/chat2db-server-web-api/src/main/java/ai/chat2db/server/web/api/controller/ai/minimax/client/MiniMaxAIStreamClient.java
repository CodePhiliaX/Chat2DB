package ai.chat2db.server.web.api.controller.ai.minimax.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.fastchat.interceptor.FastChatHeaderAuthorizationInterceptor;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * MiniMax AI stream client
 *
 * @author octo-patch
 */
@Slf4j
public class MiniMaxAIStreamClient {

    private static final String DEFAULT_HOST = "https://api.minimax.io/v1/chat/completions";

    private static final String DEFAULT_MODEL = "MiniMax-M2.7";

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

    /**
     * model
     */
    @Getter
    private String model;

    /**
     * okHttpClient
     */
    @Getter
    private OkHttpClient okHttpClient;

    /**
     * Construct instance object
     *
     * @param builder
     */
    public MiniMaxAIStreamClient(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiHost = StringUtils.isNotBlank(builder.apiHost) ? builder.apiHost : DEFAULT_HOST;
        this.model = StringUtils.isNotBlank(builder.model) ? builder.model : DEFAULT_MODEL;
        this.okHttpClient = new OkHttpClient
            .Builder()
            .addInterceptor(new FastChatHeaderAuthorizationInterceptor(this.apiKey))
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build();
    }

    /**
     * builder
     *
     * @return
     */
    public static MiniMaxAIStreamClient.Builder builder() {
        return new MiniMaxAIStreamClient.Builder();
    }

    /**
     * builder
     */
    public static final class Builder {
        private String apiKey;

        private String apiHost;

        private String model;

        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public MiniMaxAIStreamClient.Builder apiKey(String apiKeyValue) {
            this.apiKey = apiKeyValue;
            return this;
        }

        public MiniMaxAIStreamClient.Builder apiHost(String apiHostValue) {
            this.apiHost = apiHostValue;
            return this;
        }

        public MiniMaxAIStreamClient.Builder model(String modelValue) {
            this.model = modelValue;
            return this;
        }

        public MiniMaxAIStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public MiniMaxAIStreamClient build() {
            return new MiniMaxAIStreamClient(this);
        }
    }

    /**
     * Stream completions
     *
     * @param chatMessages
     * @param eventSourceListener
     */
    public void streamCompletions(List<FastChatMessage> chatMessages, EventSourceListener eventSourceListener) {
        if (CollectionUtils.isEmpty(chatMessages)) {
            log.error("param error: MiniMax AI Prompt cannot be empty");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error: MiniMaxAIEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        log.info("MiniMax AI, prompt:{}", chatMessages.get(chatMessages.size() - 1).getContent());
        try {
            FastChatCompletionsOptions chatCompletionsOptions = new FastChatCompletionsOptions(chatMessages);
            chatCompletionsOptions.setStream(true);
            chatCompletionsOptions.setModel(this.model);

            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String requestBody = mapper.writeValueAsString(chatCompletionsOptions);
            Request request = new Request.Builder()
                    .url(apiHost)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
            log.info("finish invoking MiniMax AI");
        } catch (Exception e) {
            log.error("MiniMax AI error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }
}
