package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDataSet;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Shared snapshot validation and prompt composition for analysis and comparison. */
public abstract class AbstractDataTaskDefinition
        extends AbstractAgentTaskDefinition<IAgentTaskInput.Data> {
    private final ObjectReader dataReader;

    protected AbstractDataTaskDefinition(AgentTaskType type, ObjectMapper mapper) {
        super(type, IAgentTaskInput.Data.class, AgentTaskCompletionMode.ON_DELIVERY);
        dataReader = Objects.requireNonNull(mapper, "mapper").readerFor(JsonNode.class)
                .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    }

    @Override
    protected final void validateInput(IAgentTaskInput.Data input) {
        validateCount(input.dataSets().size());
        Set<String> identifiers = new HashSet<>();
        for (AgentTaskDataSet dataSet : input.dataSets()) {
            if (!identifiers.add(dataSet.id())) {
                throw new IllegalArgumentException("Data set identities must be distinct");
            }
            try {
                JsonNode data = dataReader.readValue(dataSet.contentJson());
                if (data == null || (!data.isArray() && !data.isObject())) {
                    throw new IllegalArgumentException("Data set content must be a JSON array or object");
                }
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("Invalid data set JSON", exception);
            }
        }
    }

    protected final String dataPrompt(IAgentTaskInput.Data input, String instruction) {
        StringBuilder prompt = new StringBuilder(instruction)
                .append("\nUser request:\n").append(input.instruction());
        for (AgentTaskDataSet dataSet : input.dataSets()) {
            prompt.append("\nData set ").append(dataSet.id())
                    .append(" (").append(dataSet.label()).append("):\n")
                    .append(dataSet.contentJson());
        }
        return prompt.toString();
    }

    protected abstract void validateCount(int count);
}
