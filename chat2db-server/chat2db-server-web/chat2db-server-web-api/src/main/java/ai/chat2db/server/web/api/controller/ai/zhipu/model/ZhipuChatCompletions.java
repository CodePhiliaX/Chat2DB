package ai.chat2db.server.web.api.controller.ai.zhipu.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZhipuChatCompletions {

    /*
     * A unique identifier associated with this chat completions response.
     */
    private String msg;

    private int statusCode;

    private String data;

    /*
     * The collection of completions choices associated with this completions response.
     * Generally, `n` choices are generated per provided prompt with a default value of 1.
     * Token limits and other settings may limit the number of choices generated.
     */
    @JsonProperty(value = "body")
    private ZhipuChatBody body;

    /**
     * Creates an instance of ChatCompletions class.
     *
     * @param msg the id value to set.
     * @param code the created value to set.
     * @param body the body value to set.
     */
    @JsonCreator
    private ZhipuChatCompletions(
        @JsonProperty(value = "msg") String msg,
        @JsonProperty(value = "code") int code,
        @JsonProperty(value = "body") ZhipuChatBody body) {
        this.msg = msg;
        this.statusCode = code;
        this.body = body;
    }

}
