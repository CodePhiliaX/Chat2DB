package ai.chat2db.server.web.api.controller.ai.baichuan.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaichuanChatCompletions {

    /*
     * A unique identifier associated with this chat completions response.
     */
    private String msg;

    private int code;

    /*
     * The collection of completions choices associated with this completions response.
     * Generally, `n` choices are generated per provided prompt with a default value of 1.
     * Token limits and other settings may limit the number of choices generated.
     */
    @JsonProperty(value = "data")
    private BaichuanChatData data;

    /*
     * Usage information for tokens processed and generated as part of this completions operation.
     */
    private BaichuanChatCompletionsUsage usage;

    /**
     * Creates an instance of ChatCompletions class.
     *
     * @param msg the id value to set.
     * @param code the created value to set.
     * @param choices the choices value to set.
     * @param usage the usage value to set.
     */
    @JsonCreator
    private BaichuanChatCompletions(
        @JsonProperty(value = "msg") String msg,
        @JsonProperty(value = "code") int code,
        @JsonProperty(value = "data") BaichuanChatData choices,
        @JsonProperty(value = "usage") BaichuanChatCompletionsUsage usage) {
        this.msg = msg;
        this.code = code;
        this.data = choices;
        this.usage = usage;
    }

}
