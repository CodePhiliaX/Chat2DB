
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
     * 请求参数stream为true返回是delta
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * 请求参数stream为false返回是message
     */
    @JsonProperty("message")
    private Message message;
    /**
     * 请求参数stream为false返回是message
     */
    @JsonProperty("finish_reason")
    private String finishReason;

    /**
     * 请求参数stream为true返回是text
     */
    private String text;

    /**
     * 请求参数stream为true返回是logprobs
     */
    private String logprobs;
}