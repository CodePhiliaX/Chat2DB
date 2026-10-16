package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskModelService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class AiAgentTaskModelServiceImpl implements IAiAgentTaskModelService {
    private final AgentTaskDefinitionRegistry definitions;
    private final Supplier<String> identifiers;
    private final Clock clock;

    @Autowired
    public AiAgentTaskModelServiceImpl(AgentTaskDefinitionRegistry definitions) {
        this(definitions, () -> UUID.randomUUID().toString(), Clock.systemDefaultZone());
    }

    AiAgentTaskModelServiceImpl(AgentTaskDefinitionRegistry definitions,
                               Supplier<String> identifiers, Clock clock) {
        this.definitions = Objects.requireNonNull(definitions, "definitions");
        this.identifiers = Objects.requireNonNull(identifiers, "identifiers");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public AgentTask create(AiAgentTaskCreateRequest aiAgentTaskCreateRequest) {
        Objects.requireNonNull(aiAgentTaskCreateRequest, "aiAgentTaskCreateRequest");
        LocalDateTime now = LocalDateTime.now(clock);
        AgentTask task = new AgentTask(identifiers.get(), aiAgentTaskCreateRequest.userId(),
                aiAgentTaskCreateRequest.type(), aiAgentTaskCreateRequest.title(), AgentTaskStatus.OPEN,
                aiAgentTaskCreateRequest.sessionId(), aiAgentTaskCreateRequest.input(),
                aiAgentTaskCreateRequest.origin(), 1, now, now);
        definitions.require(task.type()).prepare(task);
        return task;
    }

    @Override
    public AgentTaskPlan prepare(AgentTask task) {
        return definitions.require(task.type()).prepare(task);
    }

    @Override
    public AgentTask advance(AgentTask task, IAgentTaskInput input) {
        requireNotCancelled(task);
        AgentTask advanced = new AgentTask(task.id(), task.userId(), task.type(), task.title(),
                AgentTaskStatus.OPEN, task.sessionId(), input, task.origin(),
                Math.incrementExact(task.revision()), task.gmtCreate(), LocalDateTime.now(clock));
        definitions.require(task.type()).prepare(advanced);
        return advanced;
    }

    @Override
    public AgentTaskDelivery deliver(AgentTask task, AgentTaskPlan plan, String runId, String response) {
        if (task.status() != AgentTaskStatus.OPEN) {
            throw new IllegalStateException("Task is not open");
        }
        if (!task.id().equals(plan.taskId()) || !task.userId().equals(plan.userId())
                || task.type() != plan.type() || task.revision() != plan.taskRevision()
                || !task.input().equals(plan.inputSnapshot())) {
            throw new IllegalArgumentException("Plan does not match the current task revision");
        }
        var definition = definitions.require(task.type());
        if (plan.completionMode() != definition.completionMode()) {
            throw new IllegalArgumentException("Plan completion mode does not match task definition");
        }
        var content = definition.materialize(plan.inputSnapshot(), response);
        LocalDateTime now = LocalDateTime.now(clock);
        AgentTaskResult result = new AgentTaskResult(identifiers.get(), task.id(), runId,
                plan.taskRevision(), content, now);
        AgentTaskStatus status = definition.completionMode() == AgentTaskCompletionMode.CONTINUOUS
                ? AgentTaskStatus.OPEN : AgentTaskStatus.COMPLETED;
        return new AgentTaskDelivery(withStatus(task, status, now), result);
    }

    @Override
    public AgentTask complete(AgentTask task) {
        requireNotCancelled(task);
        return task.status() == AgentTaskStatus.COMPLETED ? task
                : withStatus(task, AgentTaskStatus.COMPLETED, LocalDateTime.now(clock));
    }

    @Override
    public AgentTask cancel(AgentTask task) {
        return task.status() == AgentTaskStatus.CANCELLED ? task
                : withStatus(task, AgentTaskStatus.CANCELLED, LocalDateTime.now(clock));
    }

    private void requireNotCancelled(AgentTask task) {
        if (task.status() == AgentTaskStatus.CANCELLED) {
            throw new IllegalStateException("Task is cancelled");
        }
    }

    private AgentTask withStatus(AgentTask task, AgentTaskStatus status, LocalDateTime modified) {
        return new AgentTask(task.id(), task.userId(), task.type(), task.title(), status,
                task.sessionId(), task.input(), task.origin(), task.revision(),
                task.gmtCreate(), modified);
    }
}
