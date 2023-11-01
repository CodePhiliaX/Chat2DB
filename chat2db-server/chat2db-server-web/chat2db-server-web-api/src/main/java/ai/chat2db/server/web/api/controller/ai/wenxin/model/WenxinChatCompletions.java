
package ai.chat2db.server.web.api.controller.ai.wenxin.model;

import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatCompletionsUsage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WenxinChatCompletions {

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
    @JsonProperty(value = "is_truncated")
    private String isTruncated;

    @JsonProperty(value = "need_clear_history")
    private String needClearHistory;

    /**
     * object
     */
    private String object;

    /*
     * The collection of completions choices associated with this completions response.
     * Generally, `n` choices are generated per provided prompt with a default value of 1.
     * Token limits and other settings may limit the number of choices generated.
     */
    private String result;

    /*
     * Usage information for tokens processed and generated as part of this completions operation.
     */
    private FastChatCompletionsUsage usage;

    /**
     * Creates an instance of ChatCompletions class.
     *
     * @param id the id value to set.
     * @param created the created value to set.
     * @param result the result value to set.
     * @param usage the usage value to set.
     */
    @JsonCreator
    private WenxinChatCompletions(
        @JsonProperty(value = "id") String id,
        @JsonProperty(value = "created") int created,
        @JsonProperty(value = "is_truncated") String isTruncated,
        @JsonProperty(value = "need_clear_history") String needClearHistory,
        @JsonProperty(value = "object") String object,
        @JsonProperty(value = "result") String result,
        @JsonProperty(value = "usage") FastChatCompletionsUsage usage) {
        this.id = id;
        this.created = created;
        this.isTruncated = isTruncated;
        this.needClearHistory = needClearHistory;
        this.object = object;
        this.result = result;
        this.usage = usage;
    }
}
