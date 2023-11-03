package ai.chat2db.server.web.api.controller.ai.baichuan.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.baichuan.interceptor.BaichuanHeaderAuthorizationInterceptor;
import ai.chat2db.server.web.api.controller.ai.baichuan.model.BaichuanChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSourceListener;
import okio.BufferedSource;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Fast Chat Aligned Client
 *
 * @author moji
 */
@Slf4j
public class BaichuanAIStreamClient {

    /**
     * apikey
     */
    @Getter
    @NotNull
    private String apiKey;

    @Getter
    @NotNull
    private String secretKey;

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
    private BaichuanAIStreamClient(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiHost = builder.apiHost;
        this.model = builder.model;
        this.secretKey = builder.secretKey;
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
            .addInterceptor(new BaichuanHeaderAuthorizationInterceptor(this.apiKey, this.secretKey))
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
    public static BaichuanAIStreamClient.Builder builder() {
        return new BaichuanAIStreamClient.Builder();
    }

    /**
     * builder
     */
    public static final class Builder {
        private String apiKey;

        private String secretKey;

        private String apiHost;

        private String model;

        private String embeddingModel;

        /**
         * OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public BaichuanAIStreamClient.Builder apiKey(String apiKeyValue) {
            this.apiKey = apiKeyValue;
            return this;
        }

        public BaichuanAIStreamClient.Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        /**
         * @param apiHostValue
         * @return
         */
        public BaichuanAIStreamClient.Builder apiHost(String apiHostValue) {
            this.apiHost = apiHostValue;
            return this;
        }

        /**
         * @param modelValue
         * @return
         */
        public BaichuanAIStreamClient.Builder model(String modelValue) {
            this.model = modelValue;
            return this;
        }

        public BaichuanAIStreamClient.Builder embeddingModel(String embeddingModelValue) {
            this.embeddingModel = embeddingModelValue;
            return this;
        }

        public BaichuanAIStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public BaichuanAIStreamClient build() {
            return new BaichuanAIStreamClient(this);
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
            log.error("param error：Baichuan Chat Prompt cannot be empty");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error：Baichuan ChatEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        log.info("Baichuan AI, prompt:{}", chatMessages.get(chatMessages.size() - 1).getContent());
        try {

            BaichuanChatCompletionsOptions chatCompletionsOptions = new BaichuanChatCompletionsOptions();
            chatCompletionsOptions.setModel(this.model);
            chatCompletionsOptions.setMessages(chatMessages);

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletionsOptions);
            Request request = new Request.Builder()
                .url(apiHost)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                .build();
            //创建事件
            // 发送请求并处理响应
            try (Response response = this.okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // 读取并输出响应数据
                BufferedSource source = response.body().source();
                while (!source.exhausted()) {
                    String content = source.readUtf8Line();
                    eventSourceListener.onEvent(null, "[DATA]", null, content);
                }
                eventSourceListener.onEvent(null, "[DONE]", null, "[DONE]");
            } catch (Exception e) {
                log.error("baichuan ai error", e);
            }

            log.info("finish invoking baichuan ai");
        } catch (Exception e) {
            log.error("baichuan ai error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }

}
