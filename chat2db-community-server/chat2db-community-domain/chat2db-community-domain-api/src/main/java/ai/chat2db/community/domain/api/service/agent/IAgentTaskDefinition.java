package ai.chat2db.community.domain.api.service.agent;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;

/** Stateless task-type rules shared by every execution runtime. */
public interface IAgentTaskDefinition {
    AgentTaskType type();
    AgentTaskCompletionMode completionMode();
    AgentTaskPlan prepare(AgentTask task);
    AgentTaskResult.IContent materialize(IAgentTaskInput inputSnapshot, String response);
}
