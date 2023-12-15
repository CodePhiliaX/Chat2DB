package ai.chat2db.server.web.api.controller.ai.baichuan.model;

import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaichuanChatMessage {


    /*
     * The role associated with this message payload.
     */
    @JsonProperty(value = "role")
    private FastChatRole role;

    /*
     * The text associated with this message payload.
     */
    @JsonProperty(value = "content")
    private String content;

    /*
     * Reason for finishing
     */
    @JsonProperty(value = "finish_reason")
    private String finishReason;

}
