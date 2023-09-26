package ai.chat2db.server.web.api.controller.ai.azure.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AzureCompletions {

    /*
     * A unique identifier associated with this completions response.
     */
    @JsonProperty(value = "id")
    private String id;

    /*
     * The first timestamp associated with generation activity for this completions response,
     * represented as seconds since the beginning of the Unix epoch of 00:00 on 1 Jan 1970.
     */
    @JsonProperty(value = "created")
    private int created;

    /*
     * The collection of completions choices associated with this completions response.
     * Generally, `n` choices are generated per provided prompt with a default value of 1.
     * Token limits and other settings may limit the number of choices generated.
     */
    @JsonProperty(value = "choices")
    private List<AzureChoice> choices;

    /*
     * Usage information for tokens processed and generated as part of this completions operation.
     */
    @JsonProperty(value = "usage")
    private AzureCompletionsUsage usage;

    /**
     * Creates an instance of Completions class.
     *
     * @param id the id value to set.
     * @param created the created value to set.
     * @param choices the choices value to set.
     * @param usage the usage value to set.
     */
    @JsonCreator
    private AzureCompletions(
        @JsonProperty(value = "id") String id,
        @JsonProperty(value = "created") int created,
        @JsonProperty(value = "choices") List<AzureChoice> choices,
        @JsonProperty(value = "usage") AzureCompletionsUsage usage) {
        this.id = id;
        this.created = created;
        this.choices = choices;
        this.usage = usage;
    }

    /**
     * Get the id property: A unique identifier associated with this completions response.
     *
     * @return the id value.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get the created property: The first timestamp associated with generation activity for this completions response,
     * represented as seconds since the beginning of the Unix epoch of 00:00 on 1 Jan 1970.
     *
     * @return the created value.
     */
    public int getCreated() {
        return this.created;
    }

    /**
     * Get the choices property: The collection of completions choices associated with this completions response.
     * Generally, `n` choices are generated per provided prompt with a default value of 1. Token limits and other
     * settings may limit the number of choices generated.
     *
     * @return the choices value.
     */
    public List<AzureChoice> getChoices() {
        return this.choices;
    }

    /**
     * Get the usage property: Usage information for tokens processed and generated as part of this completions
     * operation.
     *
     * @return the usage value.
     */
    public AzureCompletionsUsage getUsage() {
        return this.usage;
    }
}
