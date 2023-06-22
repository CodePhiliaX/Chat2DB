package ai.chat2db.server.web.api.controller.ai.rest.client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.ai.rest.model.RestAiCompletion;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义AI接口client
 * @author moji
 */
@Slf4j
public class RestAiStreamClient {
    /**
     * rest api url
     */
    @Getter
    private String apiUrl;

    /**
     * 是否流式接口
     */
    @Getter
    private Boolean stream;
    /**
     * okHttpClient
     */
    @Getter
    private OkHttpClient okHttpClient;

    /**
     * 构造实例对象
     *
     * @param url
     */
    public RestAiStreamClient(String url, Boolean stream) {
        this.apiUrl = url;
        this.stream = stream;
        this.okHttpClient = new OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build();
    }

    /**
     * 请求RESTAI接口
     *
     * @param prompt
     * @param eventSourceListener
     */
    public void restCompletions(String prompt,
        EventSourceListener eventSourceListener) {
        log.info("开始调用自定义AI, prompt:{}", prompt);
        RestAiCompletion completion = new RestAiCompletion();
        completion.setPrompt(prompt);
        if (Objects.isNull(stream) || stream) {
            streamCompletions(completion, eventSourceListener);
            log.info("结束调用流式输出自定义AI");
            return;
        }
        nonStreamCompletions(completion, eventSourceListener);
        log.info("结束调用非流式输出自定义AI");
    }

    /**
     * 问答接口 stream 形式
     *
     * @param completion          open ai 参数
     * @param eventSourceListener sse监听器
     * @see ConsoleEventSourceListener
     */
    public void streamCompletions(RestAiCompletion completion, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空");
            throw new ParamBusinessException();
        }
        if (StringUtils.isBlank(completion.getPrompt())) {
            log.error("参数异常：Prompt不能为空");
            throw new ParamBusinessException();
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(completion);
            Request request = new Request.Builder()
                .url(this.apiUrl)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常", e);
            throw new ParamBusinessException();
        }
    }

    /**
     * 请求非流式输出接口
     *
     * @param completion
     * @param eventSourceListener
     */
    public void nonStreamCompletions(RestAiCompletion completion, EventSourceListener eventSourceListener) {
        if (StringUtils.isBlank(completion.getPrompt())) {
            log.error("参数异常：Prompt不能为空");
            throw new ParamBusinessException();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(completion);
            Request request = new Request.Builder()
                .url(this.apiUrl)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                .build();

            this.okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    eventSourceListener.onFailure(null, e, null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String content = responseBody.string();
                            eventSourceListener.onEvent(null, "[DATA]", null, content);
                            eventSourceListener.onEvent(null, "[DONE]", null, "[DONE]");
                        }
                    } catch (IOException e) {
                        eventSourceListener.onFailure(null, e, response);
                    }
                }
            });

        } catch (Exception e) {
            log.error("请求参数解析异常", e);
            throw new ParamBusinessException();
        }
    }

}
