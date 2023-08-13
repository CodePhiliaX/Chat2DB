package ai.chat2db.server.web.api.controller.ai.claude.client;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.claude.interceptor.ClaudeHeaderAuthorizationInterceptor;
import ai.chat2db.server.web.api.controller.ai.claude.model.ClaudeChatMessage;
import cn.hutool.http.ContentType;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 自定义AI接口client
 *
 * @author moji
 */
@Slf4j
public class ClaudeAiStreamClient {

    /**
     * apikey
     */
    @Getter
    @NotNull
    private String sessionKey;

    /**
     * endpoint
     */
    @Getter
    @NotNull
    private String orgId;

    /**
     * deployId
     */
    @Getter
    private String apiHost;

    @Getter
    private String userId;

    /**
     * okHttpClient
     */
    @Getter
    private OkHttpClient okHttpClient;


    /**
     * @param builder
     */
    private ClaudeAiStreamClient(Builder builder) {
        this.sessionKey = builder.sessionKey;
        this.orgId = builder.orgId;
        this.apiHost = builder.apiHost;
        this.userId = builder.userId;
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
            .addInterceptor(new ClaudeHeaderAuthorizationInterceptor(this.sessionKey, this.orgId))
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
    public static ClaudeAiStreamClient.Builder builder() {
        return new ClaudeAiStreamClient.Builder();
    }

    public static final class Builder {
        private String sessionKey;

        private String orgId;

        private String apiHost;

        private String userId;

        /**
         * 自定义OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public ClaudeAiStreamClient.Builder sessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
            return this;
        }

        /**
         * @param apiHost
         * @return
         */
        public ClaudeAiStreamClient.Builder apiHost(String apiHost) {
            this.apiHost = apiHost;
            return this;
        }

        /**
         * @param orgId
         * @return
         */
        public ClaudeAiStreamClient.Builder orgId(String orgId) {
            this.orgId = orgId;
            return this;
        }

        public ClaudeAiStreamClient.Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public ClaudeAiStreamClient.Builder okHttpClient(OkHttpClient val) {
            this.okHttpClient = val;
            return this;
        }

        public ClaudeAiStreamClient build() {
            return new ClaudeAiStreamClient(this);
        }

    }

    /**
     * chat
     *
     * @param claudeChatMessage
     * @param eventSourceListener
     */
    public void streamCompletions(ClaudeChatMessage claudeChatMessage, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("param error：AzureEventSourceListener cannot be empty");
            throw new ParamBusinessException();
        }
        log.info("Claude AI, prompt:{}", claudeChatMessage.getText());
        try {
            claudeChatMessage.setOrganization_uuid(this.orgId);
            claudeChatMessage.setConversation_uuid(this.userId);
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(claudeChatMessage);

            Request request = new Request.Builder()
                .url(this.apiHost)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
            log.info("finish invoking claude ai");
        } catch (Exception e) {
            log.error("claude ai error", e);
            eventSourceListener.onFailure(null, e, null);
            throw new ParamBusinessException();
        }
    }

}
