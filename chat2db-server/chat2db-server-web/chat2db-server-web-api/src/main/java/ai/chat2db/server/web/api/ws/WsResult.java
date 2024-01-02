package ai.chat2db.server.web.api.ws;


import ai.chat2db.server.tools.base.wrapper.Result;
import lombok.Data;

@Data
public class WsResult {
    /**
     * message id
     */
    private String uuid;

    /**
     * message content
     */
    private Result message;

    /**
     * message type
     */
    private String actionType;
}
