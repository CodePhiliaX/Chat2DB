package ai.chat2db.community.domain.api.model.agent.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** A user-facing delivery; generating a SQL proposal does not apply or execute it. */
public record AgentTaskResult(String id, String taskId, String runId, long taskRevision,
                              IContent content, LocalDateTime gmtCreate) {
    public AgentTaskResult {
        TaskModelChecks.requireText(id, "result id");
        TaskModelChecks.requireText(taskId, "taskId");
        TaskModelChecks.requireText(runId, "runId");
        if (taskRevision < 1) throw new IllegalArgumentException("Invalid taskRevision");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(gmtCreate, "gmtCreate");
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Message.class, name = "MESSAGE"),
            @JsonSubTypes.Type(value = SqlChange.class, name = "SQL_CHANGE"),
            @JsonSubTypes.Type(value = Analysis.class, name = "ANALYSIS"),
            @JsonSubTypes.Type(value = Comparison.class, name = "COMPARISON")
    })
    public sealed interface IContent permits Message, SqlChange, Analysis, Comparison {}

    public record Message(String text) implements IContent {
        public Message { TaskModelChecks.requireText(text, "message"); }
    }

    public record SqlChange(IAgentTaskInput.SqlSelection selection,
                            String originalSql, String proposedSql) implements IContent {
        public SqlChange {
            Objects.requireNonNull(selection, "selection");
            TaskModelChecks.requireText(originalSql, "originalSql");
            TaskModelChecks.requireText(proposedSql, "proposedSql");
            if (selection.to() - selection.from() != originalSql.length()) {
                throw new IllegalArgumentException("Original SQL must match the selected UTF-16 range");
            }
        }
    }

    public record Analysis(List<String> dataSetIds, String markdown) implements IContent {
        public Analysis {
            dataSetIds = List.copyOf(dataSetIds);
            if (dataSetIds.isEmpty()) throw new IllegalArgumentException("Analysis requires data");
            dataSetIds.forEach(id -> TaskModelChecks.requireText(id, "data set id"));
            TaskModelChecks.requireText(markdown, "analysis");
        }
    }

    public record Comparison(String leftDataSetId, String rightDataSetId,
                             String markdown) implements IContent {
        public Comparison {
            TaskModelChecks.requireText(leftDataSetId, "leftDataSetId");
            TaskModelChecks.requireText(rightDataSetId, "rightDataSetId");
            if (leftDataSetId.equals(rightDataSetId)) {
                throw new IllegalArgumentException("Comparison requires distinct data sets");
            }
            TaskModelChecks.requireText(markdown, "comparison");
        }
    }
}
