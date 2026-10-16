package ai.chat2db.community.domain.api.service.agent;

import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;
import java.util.List;

/**
 * User-scoped task application operations. It coordinates the pure model service with durable storage;
 * runtime adapters consume the returned plan separately.
 */
public interface IAiAgentTaskApplicationService {
    AgentTask create(AiAgentTaskCreateRequest aiAgentTaskCreateRequest);

    AgentTask get(String taskId, Long userId);

    List<AgentTask> list(Long userId);

    AgentTaskPlan prepare(String taskId, Long userId);

    AgentTask advance(String taskId, Long userId, IAgentTaskInput input);

    AgentTaskDelivery deliver(String taskId, Long userId, AgentTaskPlan plan,
                              String runId, String response);

    AgentTask complete(String taskId, Long userId);

    AgentTask cancel(String taskId, Long userId);

    List<AgentTaskResult> listResults(String taskId, Long userId);
}
