package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import org.springframework.stereotype.Component;

@Component
public final class ChatTaskDefinition extends AbstractAgentTaskDefinition<IAgentTaskInput.Text> {
    public ChatTaskDefinition() {
        super(AgentTaskType.CHAT, IAgentTaskInput.Text.class, AgentTaskCompletionMode.CONTINUOUS);
    }

    @Override
    protected void validateInput(IAgentTaskInput.Text input) {
        requireText(input.instruction(), "instruction");
    }

    @Override
    protected String prompt(IAgentTaskInput.Text input) {
        return input.instruction();
    }

    @Override
    protected AgentTaskResult.IContent result(IAgentTaskInput.Text input, String response) {
        return new AgentTaskResult.Message(response);
    }
}
