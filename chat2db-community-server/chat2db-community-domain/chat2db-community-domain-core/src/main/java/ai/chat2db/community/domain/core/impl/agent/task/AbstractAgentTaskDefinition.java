package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.service.agent.IAgentTaskDefinition;

/** Template for input validation, immutable plan preparation, and typed result creation. */
public abstract class AbstractAgentTaskDefinition<I extends IAgentTaskInput>
        implements IAgentTaskDefinition {
    private final AgentTaskType type;
    private final Class<I> inputType;
    private final AgentTaskCompletionMode completionMode;

    protected AbstractAgentTaskDefinition(AgentTaskType type, Class<I> inputType,
                                          AgentTaskCompletionMode completionMode) {
        this.type = type;
        this.inputType = inputType;
        this.completionMode = completionMode;
    }

    @Override
    public final AgentTaskType type() {
        return type;
    }

    @Override
    public final AgentTaskCompletionMode completionMode() {
        return completionMode;
    }

    @Override
    public final AgentTaskPlan prepare(AgentTask task) {
        if (task.type() != type || task.status() != AgentTaskStatus.OPEN) {
            throw new IllegalArgumentException("Task must be open and have type " + type);
        }
        I input = checkedInput(task.input());
        validateInput(input);
        return new AgentTaskPlan(task.id(), task.userId(), type, task.revision(),
                input, resolvePrompt(input), completionMode);
    }

    /** The caller's prompt replaces the task definition's default for this revision. */
    private String resolvePrompt(I input) {
        return input.promptOverride() != null ? input.promptOverride() : prompt(input);
    }

    @Override
    public final AgentTaskResult.IContent materialize(IAgentTaskInput inputSnapshot, String response) {
        I input = checkedInput(inputSnapshot);
        validateInput(input);
        requireText(response, "response");
        return result(input, response);
    }

    private I checkedInput(IAgentTaskInput input) {
        if (!inputType.isInstance(input)) {
            throw new IllegalArgumentException(type + " requires input " + inputType.getSimpleName());
        }
        return inputType.cast(input);
    }

    protected static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    protected abstract void validateInput(I input);
    protected abstract String prompt(I input);
    protected abstract AgentTaskResult.IContent result(I input, String response);
}
