package ai.chat2db.community.domain.api.model.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import java.util.Objects;

/** A run consumes this immutable plan, rather than mutable editor or task state. */
public record AgentTaskPlan(String taskId, Long userId, AgentTaskType type, long taskRevision,
                            IAgentTaskInput inputSnapshot, String prompt,
                            AgentTaskCompletionMode completionMode) {
    public AgentTaskPlan {
        TaskModelChecks.requireText(taskId, "taskId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(type, "type");
        if (taskRevision < 1) throw new IllegalArgumentException("Invalid taskRevision");
        Objects.requireNonNull(inputSnapshot, "inputSnapshot");
        TaskModelChecks.requireText(prompt, "prompt");
        Objects.requireNonNull(completionMode, "completionMode");
    }
}
