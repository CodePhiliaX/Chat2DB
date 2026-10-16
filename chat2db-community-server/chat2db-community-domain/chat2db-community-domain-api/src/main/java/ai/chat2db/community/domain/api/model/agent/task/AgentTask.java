package ai.chat2db.community.domain.api.model.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Stable business identity, independent of execution runtime and native session state.
 * sessionId references the application's conversation, not a runtime's native session;
 * revision identifies the current input, not a persistence version.
 */
public record AgentTask(String id, Long userId, AgentTaskType type, String title,
                        AgentTaskStatus status, String sessionId, IAgentTaskInput input,
                        Origin origin, long revision,
                        LocalDateTime gmtCreate, LocalDateTime gmtModified) {
    public AgentTask {
        TaskModelChecks.requireText(id, "task id");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(type, "type");
        TaskModelChecks.requireText(title, "title");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(gmtCreate, "gmtCreate");
        Objects.requireNonNull(gmtModified, "gmtModified");
        if (revision < 1) throw new IllegalArgumentException("revision must be positive");
        if (type == AgentTaskType.CHAT || sessionId != null) {
            TaskModelChecks.requireText(sessionId, "logical session id");
        }
    }

    public record Origin(String entrypoint, String resourceId, String sourceTaskId) {
        public Origin {
            TaskModelChecks.requireText(entrypoint, "entrypoint");
            TaskModelChecks.requireText(resourceId, "resourceId");
            if (sourceTaskId != null) TaskModelChecks.requireText(sourceTaskId, "sourceTaskId");
        }
    }
}
