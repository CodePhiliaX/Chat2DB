package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import org.springframework.stereotype.Component;

@Component
public final class SqlRepairTaskDefinition extends AbstractAgentTaskDefinition<IAgentTaskInput.Sql> {
    public SqlRepairTaskDefinition() {
        super(AgentTaskType.SQL_REPAIR, IAgentTaskInput.Sql.class, AgentTaskCompletionMode.ON_DELIVERY);
    }

    @Override
    protected void validateInput(IAgentTaskInput.Sql input) {
        requireText(input.error(), "SQL error");
    }

    @Override
    protected String prompt(IAgentTaskInput.Sql input) {
        return """
                Repair the selected SQL using the supplied error and database context.
                Preserve its intended semantics. Return only the corrected SQL.
                Do not apply changes to the editor or execute SQL.

                SQL:
                """ + input.sql() + "\nError:\n" + input.error()
                + "\nAdditional request:\n" + input.instruction();
    }

    @Override
    protected AgentTaskResult.IContent result(IAgentTaskInput.Sql input, String response) {
        return new AgentTaskResult.SqlChange(input.selection(), input.sql(), response.strip());
    }
}
