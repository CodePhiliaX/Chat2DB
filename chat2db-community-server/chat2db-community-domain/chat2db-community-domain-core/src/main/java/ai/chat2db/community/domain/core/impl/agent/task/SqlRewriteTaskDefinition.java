package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import org.springframework.stereotype.Component;

@Component
public final class SqlRewriteTaskDefinition extends AbstractAgentTaskDefinition<IAgentTaskInput.Sql> {
    public SqlRewriteTaskDefinition() {
        super(AgentTaskType.SQL_REWRITE, IAgentTaskInput.Sql.class, AgentTaskCompletionMode.ON_DELIVERY);
    }

    @Override
    protected void validateInput(IAgentTaskInput.Sql input) {
        requireText(input.instruction(), "rewrite instruction");
    }

    @Override
    protected String prompt(IAgentTaskInput.Sql input) {
        return """
                Rewrite the selected SQL according to the user's request.
                Preserve semantics except for explicitly requested changes.
                Return only the rewritten SQL. Do not apply or execute it.

                Request:
                """ + input.instruction() + "\nSQL:\n" + input.sql();
    }

    @Override
    protected AgentTaskResult.IContent result(IAgentTaskInput.Sql input, String response) {
        return new AgentTaskResult.SqlChange(input.selection(), input.sql(), response.strip());
    }
}
