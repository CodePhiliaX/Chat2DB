package ai.chat2db.server.web.api.controller.ai.claude.model;

import lombok.Data;

@Data
public class ClaudeChatMessage {

    private String conversation_uuid;

    private String organization_uuid;

    private String text;

    private ClaudeChatCompletionsOptions completion;
}
