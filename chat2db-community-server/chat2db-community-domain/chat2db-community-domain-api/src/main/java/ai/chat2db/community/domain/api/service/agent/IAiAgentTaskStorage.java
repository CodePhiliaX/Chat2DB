package ai.chat2db.community.domain.api.service.agent;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import java.util.List;

/**
 * Durable storage for task snapshots and delivered results.
 * Implementations own authorization and compare-and-set semantics; callers never write files directly.
 */
public interface IAiAgentTaskStorage {
    AgentTask create(AgentTask task);

    AgentTask get(String taskId, Long userId);

    List<AgentTask> list(Long userId);

    boolean compareAndSet(AgentTask task, AgentTaskStatus expectedStatus, long expectedRevision, Long userId);

    /** Saves a task transition and its result under one storage monitor and expected snapshot. */
    AgentTaskDelivery saveDelivery(AgentTaskDelivery delivery, AgentTaskStatus expectedStatus,
                                   long expectedRevision, Long userId);

    AgentTaskResult createResult(AgentTaskResult result, Long userId);

    List<AgentTaskResult> listResults(String taskId, Long userId);
}
