
package ai.chat2db.server.web.api.controller.ai.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.unfbx.chatgpt.entity.common.Usage;
import lombok.Data;

/**
 * @author jipengfei
 * @version : ChatCompletionResponse.java
 */
@Data
public class ChatCompletionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 4968922211204353592L;
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatChoice> choices;
    private Usage usage;
}