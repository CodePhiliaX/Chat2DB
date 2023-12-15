package ai.chat2db.server.web.api.controller.ai.fastchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FastChatMessage {


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

    /**
     * Creates an instance of ChatMessage class.
     *
     * @param role the role value to set.
     */
    @JsonCreator
    public FastChatMessage(@JsonProperty(value = "role") FastChatRole role) {
        this.role = role;
    }

    /**
     * Get the role property: The role associated with this message payload.
     *
     * @return the role value.
     */
    public FastChatRole getRole() {
        return this.role;
    }

    /**
     * Get the content property: The text associated with this message payload.
     *
     * @return the content value.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Set the content property: The text associated with this message payload.
     *
     * @param content the content value to set.
     * @return the ChatMessage object itself.
     */
    public FastChatMessage setContent(String content) {
        this.content = content;
        return this;
    }
}
