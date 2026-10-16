package ai.chat2db.community.domain.api.model.agent.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IAgentTaskInput.Text.class, name = "TEXT"),
        @JsonSubTypes.Type(value = IAgentTaskInput.Sql.class, name = "SQL"),
        @JsonSubTypes.Type(value = IAgentTaskInput.Data.class, name = "DATA")
})
public sealed interface IAgentTaskInput
        permits IAgentTaskInput.Text, IAgentTaskInput.Sql, IAgentTaskInput.Data {
    String instruction();
    AgentTaskContext context();

    /**
     * Caller-supplied prompt that replaces the task definition's default for this revision.
     * {@code null} keeps the default; a blank value is invalid.
     */
    String promptOverride();

    record Text(String instruction, AgentTaskContext context, String promptOverride)
            implements IAgentTaskInput {
        public Text {
            TaskModelChecks.requireText(instruction, "instruction");
            Objects.requireNonNull(context, "context");
            TaskModelChecks.optionalText(promptOverride, "prompt override");
        }
    }

    /** Empty instruction/error represents an absent optional menu argument. */
    record Sql(String instruction, String sql, String error, SqlSelection selection,
               AgentTaskContext context, String promptOverride) implements IAgentTaskInput {
        public Sql {
            instruction = Objects.requireNonNullElse(instruction, "");
            error = Objects.requireNonNullElse(error, "");
            TaskModelChecks.requireText(sql, "sql");
            Objects.requireNonNull(selection, "selection");
            Objects.requireNonNull(context, "context");
            TaskModelChecks.optionalText(promptOverride, "prompt override");
            if (selection.to() - selection.from() != sql.length()) {
                throw new IllegalArgumentException("SQL must match the selected UTF-16 range");
            }
        }
    }

    record SqlSelection(String documentId, long documentVersion, int from, int to) {
        public SqlSelection {
            TaskModelChecks.requireText(documentId, "documentId");
            if (documentVersion < 0 || from < 0 || to < from) {
                throw new IllegalArgumentException("Invalid SQL selection");
            }
        }
    }

    record Data(String instruction, List<AgentTaskDataSet> dataSets,
                AgentTaskContext context, String promptOverride) implements IAgentTaskInput {
        public Data {
            TaskModelChecks.requireText(instruction, "instruction");
            dataSets = List.copyOf(dataSets);
            Objects.requireNonNull(context, "context");
            TaskModelChecks.optionalText(promptOverride, "prompt override");
        }
    }
}
