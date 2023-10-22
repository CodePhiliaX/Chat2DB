package ai.chat2db.server.web.api.controller.ai.fastchat.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.fastchat.embeddings.FastChatEmbedding;
import ai.chat2db.server.web.api.controller.ai.fastchat.embeddings.FastChatEmbeddingResponse;
import ai.chat2db.server.web.api.controller.ai.fastchat.interceptor.FastChatHeaderAuthorizationInterceptor;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Fast Chat Aligned Client
 *
 * @author moji
 */
@Slf4j
public class FastChatAIStreamClient {

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
     * embeddingModel
     */
    @Getter
    private String embeddingModel;

    /**
     * okHttpClient
     */
    @Getter
    private OkHttpClient okHttpClient;

    @Getter
    private FastChatOpenAiApi fastChatOpenAiApi;


    /**
     * @param builder
     */
    private FastChatAIStreamClient(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiHost = builder.apiHost;
        this.model = builder.model;
        this.embeddingModel = builder.embeddingModel;
        if (Objects.isNull(builder.okHttpClient)) {
            builder.okHttpClient = this.okHttpClient();
        }
        okHttpClient = builder.okHttpClient;
        this.fastChatOpenAiApi = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(FastChatOpenAiApi.class);
    }

    /**
     * okhttpclient
     */
    private OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .addInterceptor(new FastChatHeaderAuthorizationInterceptor(this.apiKey))
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
    public static FastChatAIStreamClient.Builder builder() {
        return new FastChatAIStreamClient.Builder();
    }

    /**
     * builder
     */
    public static final class Builder {
        private String apiKey;

        private String apiHost;

        private String model;

        private String embeddingModel;

        /**
         * OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public FastChatAIStreamClient.Builder apiKey(String apiKeyValue) {
            this.apiKey = apiKeyValue;
            return this;
        }

        /**
         * @param apiHostValue
         * @return
         */
        public FastChatAIStreamClient.Builder apiHost(String apiHostValue) {
            this.apiHost = apiHostValue;
            return this;
        }

        /**
         * @param modelValue
         * @return
         */
        public FastChatAIStreamClient.Builder model(String modelValue) {
            this.model = modelValue;
            return this;
        }

        public FastChatAIStreamClient.Builder embeddingModel(String embeddingModelValue) {
            this.embeddingModel = embeddingModelValue;
            return this;
        }

        public FastChatAIStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public FastChatAIStreamClient build() {
            return new FastChatAIStreamClient(this);
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
            log.error("param error：Fast Chat Prompt cannot be empty");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error：FastChatEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        log.info("Fast Chat AI, prompt:{}", chatMessages.get(chatMessages.size() - 1).getContent());
        try {

            FastChatCompletionsOptions chatCompletionsOptions = new FastChatCompletionsOptions(chatMessages);
            chatCompletionsOptions.setStream(true);
            chatCompletionsOptions.setModel(this.model);

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
            log.error("fast chat ai error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param input
     * @return EmbeddingResponse
     */
    public FastChatEmbeddingResponse embeddings(String input) {
        FastChatEmbedding embedding = FastChatEmbedding.builder().input(input).build();
        if (StringUtils.isNotBlank(this.embeddingModel)) {
            embedding.setModel(this.embeddingModel);
        }
        return this.embeddings(embedding);
    }

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param embedding
     * @return EmbeddingResponse
     */
    public FastChatEmbeddingResponse embeddings(FastChatEmbedding embedding) {
        Single<FastChatEmbeddingResponse> embeddings = this.fastChatOpenAiApi.embeddings(embedding);
        return embeddings.blockingGet();
    }
}
