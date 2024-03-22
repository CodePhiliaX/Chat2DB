package ai.chat2db.server.web.api.controller.ai.azure.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AzureChatMessage {


    /*
     * The role associated with this message payload.
     */
    @JsonProperty(value = "role")
    private AzureChatRole role;

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
    public AzureChatMessage(@JsonProperty(value = "role") AzureChatRole role) {
        this.role = role;
    }

    /**
     * Get the role property: The role associated with this message payload.
     *
     * @return the role value.
     */
    public AzureChatRole getRole() {
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
    public AzureChatMessage setContent(String content) {
        this.content = content;
        return this;
    }
}
