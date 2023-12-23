package ai.chat2db.server.web.api.controller.ai.dify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DifyChatStreamEvent {

    private String event;
    @JsonProperty("task_id")
    private String taskId;
    private String id;
    private String answer;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("conversation_id")
    private String conversationId;

}
