package ai.chat2db.server.web.api.controller.ai.minimax.model;

import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatChoice;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * MiniMax AI chat completions response
 *
 * @author octo-patch
 */
@Data
public class MiniMaxChatCompletions {

    /*
     * A unique identifier associated with this chat completions response.
     */
    private String id;

    /*
     * The first timestamp associated with generation activity for this completions response,
     * represented as seconds since the beginning of the Unix epoch of 00:00 on 1 Jan 1970.
     */
    private int created;

    /**
     * model
     */
    private String model;

    /**
     * object
     */
    private String object;

    /*
     * The collection of completions choices associated with this completions response.
     */
    @JsonProperty(value = "choices")
    private List<FastChatChoice> choices;

    @JsonCreator
    private MiniMaxChatCompletions(
        @JsonProperty(value = "id") String id,
        @JsonProperty(value = "created") int created,
        @JsonProperty(value = "model") String model,
        @JsonProperty(value = "object") String object,
        @JsonProperty(value = "choices") List<FastChatChoice> choices) {
        this.id = id;
        this.created = created;
        this.model = model;
        this.object = object;
        this.choices = choices;
    }

    public String getId() {
        return this.id;
    }

    public int getCreated() {
        return this.created;
    }

    public List<FastChatChoice> getChoices() {
        return this.choices;
    }
}
