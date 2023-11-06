package ai.chat2db.server.web.api.controller.ai.wenxin.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import ai.chat2db.server.web.api.controller.ai.wenxin.interceptor.AccessTokenInterceptor;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Fast Chat Aligned Client
 *
 * @author moji
 */
@Slf4j
public class WenxinAIStreamClient {

    /**
     * apikey
     */
    @Getter
    @NotNull
    private String accessToken;

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
     * embeddingModel
     */
    @Getter
    private String embeddingModel;

    /**
     * okHttpClient
     */
    @Getter
    private OkHttpClient okHttpClient;


    /**
     * @param builder
     */
    private WenxinAIStreamClient(Builder builder) {
        this.accessToken = builder.accessToken;
        this.apiHost = builder.apiHost;
        this.model = builder.model;
        this.embeddingModel = builder.embeddingModel;
        if (Objects.isNull(builder.okHttpClient)) {
            builder.okHttpClient = this.okHttpClient();
        }
        okHttpClient = builder.okHttpClient;
    }

    /**
     * okhttpclient
     */
    private OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .addInterceptor(new AccessTokenInterceptor(this.accessToken))
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build();
        return okHttpClient;
    }

    /**
     * 构造
     *
     * @return
     */
    public static WenxinAIStreamClient.Builder builder() {
        return new WenxinAIStreamClient.Builder();
    }

    /**
     * builder
     */
    public static final class Builder {
        private String accessToken;

        private String apiHost;

        private String model;

        private String embeddingModel;

        /**
         * OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public WenxinAIStreamClient.Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        /**
         * @param apiHostValue
         * @return
         */
        public WenxinAIStreamClient.Builder apiHost(String apiHostValue) {
            this.apiHost = apiHostValue;
            return this;
        }

        /**
         * @param modelValue
         * @return
         */
        public WenxinAIStreamClient.Builder model(String modelValue) {
            this.model = modelValue;
            return this;
        }

        public WenxinAIStreamClient.Builder embeddingModel(String embeddingModelValue) {
            this.embeddingModel = embeddingModelValue;
            return this;
        }

        public WenxinAIStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public WenxinAIStreamClient build() {
            return new WenxinAIStreamClient(this);
        }

    }

    /**
     * 问答接口 stream 形式
     *
     * @param chatMessages
     * @param eventSourceListener
     */
    public void streamCompletions(List<FastChatMessage> chatMessages, EventSourceListener eventSourceListener) {
        if (CollectionUtils.isEmpty(chatMessages)) {
            log.error("param error：Wenxin Prompt cannot be empty");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error：WenxinEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        log.info("Wenxin Chat AI, prompt:{}", chatMessages.get(chatMessages.size() - 1).getContent());
        try {

            FastChatCompletionsOptions chatCompletionsOptions = new FastChatCompletionsOptions(chatMessages);
            chatCompletionsOptions.setStream(true);

            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletionsOptions);
            Request request = new Request.Builder()
                .url(apiHost)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
            log.info("finish invoking fast chat ai");
        } catch (Exception e) {
            log.error("wenxin chat ai error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }


}
