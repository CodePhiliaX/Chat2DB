package ai.chat2db.server.web.api.controller.ai.tongyi.model;


import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import lombok.Data;

import java.util.List;

@Data
public class TongyiChatMessage {

    private List<FastChatMessage> messages;
}
