package ai.chat2db.server.web.api.controller.ai.openai.listener;

import ai.chat2db.server.web.api.controller.ai.openai.client.OpenAIClient;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.response.ChatCompletionResponse;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.BaseMessage;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.chat.tool.ToolCallFunction;
import com.unfbx.chatgpt.entity.chat.tool.ToolCalls;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class OpenAIEventSourceListener extends EventSourceListener {

    private final SseEmitter sseEmitter;

    private final List<Message> messages;

    private final ConnectInfo connectInfo;

    private final ChatQueryRequest queryRequest;

    private List<ToolCalls> toolCalls = new ArrayList<>();


    public OpenAIEventSourceListener(SseEmitter sseEmitter, List<Message> messages, ConnectInfo connectInfo, ChatQueryRequest queryRequest) {
        this.sseEmitter = sseEmitter;
        this.messages = messages;
        this.connectInfo = connectInfo;
        this.queryRequest = queryRequest;
    }

    public static List<ToolCalls> mergeToolCallsLists(List<ToolCalls> list1, List<ToolCalls> list2) {
        List<ToolCalls> mergedList = new ArrayList<>(list1);
        if (list2.isEmpty()) {
            return mergedList;
        }
        ToolCalls item2 = list2.get(0);
        boolean isMerged = false;
        // 反向遍历
        for (int i = list1.size() - 1; i >= 0; i--) {
            ToolCalls item1 = list1.get(i);
            if (item2.getId() == null || Objects.equals(item1.getId(), item2.getId())) {
                mergedList.set(i, mergeToolCalls(item1, item2));
                isMerged = true;
                break;
            }
        }
        if (!isMerged) {
            // 如果 list2 中的对象与 list1 中的任何对象都不匹配，则作为新对象添加
            mergedList.add(item2);
        }
        return mergedList;
    }

    private static ToolCalls mergeToolCalls(ToolCalls tc1, ToolCalls tc2) {
        if (tc1 == null) return tc2;
        if (tc2 == null) return tc1;

        // 相同的逻辑，只是当 id 为 null 时进行合并
        String id = tc1.getId() != null ? tc1.getId() : tc2.getId();
        String type = mergeStrings(tc1.getType(), tc2.getType());
        ToolCallFunction function = mergeToolCallFunctions(tc1.getFunction(), tc2.getFunction());

        return new ToolCalls(id, type, function);
    }

    private static ToolCallFunction mergeToolCallFunctions(ToolCallFunction f1, ToolCallFunction f2) {
        if (f1 == null) return f2;
        if (f2 == null) return f1;

        String name = mergeStrings(f1.getName(), f2.getName());
        String arguments = mergeStrings(f1.getArguments(), f2.getArguments());

        return new ToolCallFunction(name, arguments);
    }

    private static String mergeStrings(String str1, String str2) {
        if (str1 != null && str2 != null) {
            // Concatenate both strings
            return str1 + str2;
        } else if (str1 != null) {
            return str1;
        } else {
            return str2;
        }
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
            if (toolCalls.isEmpty()) {
                log.info("OpenAI返回数据结束了");
                sseEmitter.send(SseEmitter.event()
                        .id("[DONE]")
                        .data("[DONE]")
                        .reconnectTime(3000));
                sseEmitter.complete();
                return;
            }
            messages.add(Message.builder()
                    .toolCalls(toolCalls)
                    .role(BaseMessage.Role.ASSISTANT).build());
            Chat2DBContext.putContext(connectInfo);
            try {
                for (ToolCalls toolCall : toolCalls) {
                    String callId = toolCall.getId();
                    ToolCallFunction function = toolCall.getFunction();
                    if (function != null && Objects.nonNull(function.getArguments())) {
                        String functionName = function.getName();
                        JSONObject arguments = JSONObject.parse(function.getArguments());
                        if ("get_table_columns".equals(functionName)) {
                            MetaData metaSchema = Chat2DBContext.getMetaData();
                            String ddl = metaSchema.tableDDL(Chat2DBContext.getConnection(), queryRequest.getDatabaseName(), queryRequest.getSchemaName(), arguments.getString("table_name"));
                            messages.add(Message.builder().role(BaseMessage.Role.TOOL)
                                    .toolCallId(callId)
                                    .name(functionName)
                                    .content(ddl)
                                    .build());
                        }
                    }
                }
            } finally {
                Chat2DBContext.removeContext();
            }
            OpenAIClient.getInstance().streamChatCompletion(messages, this);
            toolCalls.clear();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 读取Json
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        Message delta = completionResponse.getChoices().get(0).getDelta();
        if (delta != null && delta.getToolCalls() != null) {
            this.toolCalls = mergeToolCallsLists(this.toolCalls, delta.getToolCalls());
        }
        String text = delta == null
                ? completionResponse.getChoices().get(0).getText()
                : delta.getContent();
        Message message = new Message();
        if (text != null) {
            message.setContent(text);
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(message)
                    .reconnectTime(3000));
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
//        sseEmitter.complete();
//        log.info("OpenAI关闭sse连接...");
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
