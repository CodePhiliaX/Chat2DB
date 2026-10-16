package ai.chat2db.community.domain.api.model.agent.task;

import java.util.Objects;

/** The caller persists the task update and result together with the corresponding run. */
public record AgentTaskDelivery(AgentTask task, AgentTaskResult result) {
    public AgentTaskDelivery {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(result, "result");
        if (!task.id().equals(result.taskId()) || task.revision() != result.taskRevision()) {
            throw new IllegalArgumentException("Result does not belong to the task revision");
        }
    }
}
