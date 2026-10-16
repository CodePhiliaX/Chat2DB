package ai.chat2db.community.domain.api.service.agent;

import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;

/**
 * Creates and transitions domain snapshots. Execution and persistence belong to their
 * existing services; these operations neither start nor stop a runtime.
 * Callers authorize the task owner and resource references, verify run ownership, and
 * persist transitions with concurrency control.
 */
public interface IAiAgentTaskModelService {
    AgentTask create(AiAgentTaskCreateRequest aiAgentTaskCreateRequest);
    AgentTaskPlan prepare(AgentTask task);
    AgentTask advance(AgentTask task, IAgentTaskInput input);
    AgentTaskDelivery deliver(AgentTask task, AgentTaskPlan plan, String runId, String response);
    AgentTask complete(AgentTask task);
    AgentTask cancel(AgentTask task);
}
