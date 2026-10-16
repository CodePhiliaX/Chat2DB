package ai.chat2db.community.domain.api.model.agent.task;

/**
 * Immutable selected-data snapshot. JSON preserves cell types and null values.
 * source may be absent for uploaded or manually supplied data.
 */
public record AgentTaskDataSet(String id, String label, String contentJson,
                               AgentTaskContext.Scope source) {
    public AgentTaskDataSet {
        TaskModelChecks.requireText(id, "data set id");
        TaskModelChecks.requireText(label, "data set label");
        TaskModelChecks.requireText(contentJson, "data set content");
    }
}
