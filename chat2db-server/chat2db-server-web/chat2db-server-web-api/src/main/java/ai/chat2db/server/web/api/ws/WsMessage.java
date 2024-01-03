package ai.chat2db.server.web.api.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class WsMessage {

    /**
     * message id
     */
    private String uuid;

    /**
     * message content
     */
    private JSONObject message;

    /**
     * message type
     */
    private String actionType;


    public static class ActionType {
        public static final String EXECUTE = "execute";
        public static final String LOGIN = "login";
        public static final String PING = "ping";
        public static final String OPEN_SESSION = "open_session";
        public static final String ERROR = "error";
        public static final String MESSAGE = "message";
    }

}
