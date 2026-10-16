package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskApplicationService;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskModelService;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskStorage;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class AiAgentTaskApplicationServiceImpl implements IAiAgentTaskApplicationService {
    private final IAiAgentTaskModelService modelService;
    private final IAiAgentTaskStorage storage;

    @Autowired
    public AiAgentTaskApplicationServiceImpl(IAiAgentTaskModelService modelService,
                                             IAiAgentTaskStorage storage) {
        this.modelService = Objects.requireNonNull(modelService, "modelService");
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    @Override
    public AgentTask create(AiAgentTaskCreateRequest aiAgentTaskCreateRequest) {
        return storage.create(modelService.create(aiAgentTaskCreateRequest));
    }

    @Override
    public AgentTask get(String taskId, Long userId) {
        return requireTask(taskId, userId);
    }

    @Override
    public List<AgentTask> list(Long userId) {
        Objects.requireNonNull(userId, "userId");
        return storage.list(userId);
    }

    @Override
    public AgentTaskPlan prepare(String taskId, Long userId) {
        return modelService.prepare(requireTask(taskId, userId));
    }

    @Override
    public AgentTask advance(String taskId, Long userId, IAgentTaskInput input) {
        AgentTask current = requireTask(taskId, userId);
        AgentTask next = modelService.advance(current, input);
        if (!storage.compareAndSet(next, current.status(), current.revision(), userId)) {
            throw new IllegalStateException("Agent task changed while advancing: " + taskId);
        }
        return next;
    }

    @Override
    public AgentTaskDelivery deliver(String taskId, Long userId, AgentTaskPlan plan,
                                     String runId, String response) {
        AgentTask current = requireTask(taskId, userId);
        AgentTaskDelivery delivery = modelService.deliver(current, plan, runId, response);
        return storage.saveDelivery(delivery, current.status(), current.revision(), userId);
    }

    @Override
    public AgentTask complete(String taskId, Long userId) {
        return transition(taskId, userId, modelService::complete);
    }

    @Override
    public AgentTask cancel(String taskId, Long userId) {
        return transition(taskId, userId, modelService::cancel);
    }

    @Override
    public List<AgentTaskResult> listResults(String taskId, Long userId) {
        requireTask(taskId, userId);
        return storage.listResults(taskId, userId);
    }

    private AgentTask transition(String taskId, Long userId,
                                 java.util.function.Function<AgentTask, AgentTask> operation) {
        AgentTask current = requireTask(taskId, userId);
        AgentTask next = operation.apply(current);
        if (next == current) {
            return current;
        }
        if (!storage.compareAndSet(next, current.status(), current.revision(), userId)) {
            throw new IllegalStateException("Agent task changed while transitioning: " + taskId);
        }
        return next;
    }

    private AgentTask requireTask(String taskId, Long userId) {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("taskId must not be blank");
        }
        Objects.requireNonNull(userId, "userId");
        AgentTask task = storage.get(taskId, userId);
        if (task == null) {
            throw new IllegalArgumentException("Agent task does not exist");
        }
        return task;
    }
}
