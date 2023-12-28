package ai.chat2db.server.web.api.controller.ai.chat2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DifyChatCompletionsOptions {
    /**
     * （选填）以键值对方式提供用户输入字段，与提示词编排中的变量对应。Key 为变量名称，Value 是参数值。
     * 如果字段类型为 Select，传入的 Value 需为预设选项之一。
     */
    private Map<String, String> inputs;

    /**
     * 用户输入/提问内容
     */
    private String query;

    /**
     * blocking 阻塞型，等待执行完毕后返回结果。（请求若流程较长可能会被中断）
     * streaming 流式返回。基于 SSE（Server-Sent Events）实现流式返回。
     */
    @JsonProperty("response_mode")
    private String responseMode = "blocking";

    /**
     * （必填）‼️ 会话标识符，首次对话为 conversation_id: "" ‼️，如果要继续对话请传入上下文返回的 conversation_id
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 用户标识，由开发者定义规则，需保证用户标识在应用内唯一。
     */
    private String user;


}
