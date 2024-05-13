
package ai.chat2db.server.web.api.controller.ai.response;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

/**
 * @author jipengfei
 * @version : ChatChoice.java
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatChoice implements Serializable {
    @Serial
    private static final long serialVersionUID = 6347129660363472014L;

    private long index;
    /**
     * If the request parameter stream is true, the return value is delta.
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * If the request parameter stream is false, the return value is message.
     */
    @JsonProperty("message")
    private Message message;
    /**
     * If the request parameter stream is false, the return value is message.
     */
    @JsonProperty("finish_reason")
    private String finishReason;

    /**
     * If the request parameter stream is true, the return value is text.
     */
    private String text;

    /**
     * If the request parameter stream is true, the return value is logprobs.
     */
    private String logprobs;
}