package ai.chat2db.community.domain.api.model.request.agent;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** sessionId is required for CHAT and optional for a bounded task. */
public record AiAgentTaskCreateRequest(@NotNull Long userId,
                                      @NotNull AgentTaskType type,
                                      @NotBlank String title,
                                      String sessionId,
                                      @NotNull @Valid IAgentTaskInput input,
                                      @NotNull @Valid AgentTask.Origin origin) {}
