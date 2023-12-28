package ai.chat2db.server.web.api.controller.ai.dify.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.azure.interceptor.AzureHeaderAuthorizationInterceptor;
import ai.chat2db.server.web.api.controller.ai.chat2db.model.DifyChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.dify.listener.DifyChatAIEventSourceListener;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hpsf.GUID;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j

public class DifyChatAiStreamClient {

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

    @Getter
    private OkHttpClient okHttpClient;

    private DifyChatAiStreamClient(DifyChatAiStreamClient.Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiHost = builder.apiHost;
        if (Objects.isNull(builder.okHttpClient)) {
            builder.okHttpClient = this.okHttpClient();
        }
        okHttpClient = builder.okHttpClient;
    }


    /**
     * 构造
     *
     * @return
     */
    public static DifyChatAiStreamClient.Builder builder() {
        return new DifyChatAiStreamClient.Builder();
    }

    /**
     * okhttpclient
     */
    private OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(new AzureHeaderAuthorizationInterceptor(this.apiKey))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    public static final class Builder {
        private String apiKey;

        private String apiHost;

        /**
         * 自定义OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public DifyChatAiStreamClient.Builder apiKey(String apiKeyValue) {
            this.apiKey = apiKeyValue;
            return this;
        }

        /**
         * @param apiHost
         * @return
         */
        public DifyChatAiStreamClient.Builder apiHost(String apiHost) {
            this.apiHost = apiHost;
            return this;
        }


        public DifyChatAiStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public DifyChatAiStreamClient build() {
            return new DifyChatAiStreamClient(this);
        }

    }


    public void streamCompletions(List<Message> messages, DifyChatAIEventSourceListener eventSourceListener,
                                  String uid, String conversationId) {
        if (CollectionUtils.isEmpty(messages)) {
            log.error("param error：Dify Chat Prompt cannot be empty");
            throw new ParamBusinessException("prompt");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error：DifyChatAIEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        String lastMessage = messages.get(messages.size() - 1).getContent();
        log.info("Dify Chat AI, uid:{} conversationId:{} prompt:{}", uid, conversationId, lastMessage);

        try {
            DifyChatCompletionsOptions chatCompletionsOptions = new DifyChatCompletionsOptions();
            chatCompletionsOptions.setQuery(lastMessage);
            chatCompletionsOptions.setResponseMode("streaming");
            chatCompletionsOptions.setConversationId(conversationId);
            chatCompletionsOptions.setUser(uid);

            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String requestBody = mapper.writeValueAsString(chatCompletionsOptions);
            if (!apiHost.endsWith("/")) {
                apiHost = apiHost + "/";
            }
            String url = this.apiHost + "v1/chat-messages";
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization","Bearer "+apiKey)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
            log.info("finish invoking Dify Chat ai");
        } catch (Exception e) {
            log.error("Dify Chat error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }
}
