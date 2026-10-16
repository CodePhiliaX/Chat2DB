package ai.chat2db.community.domain.api.enums.agent;

/** Business lifecycle; an individual run failure does not terminate an open task. */
public enum AgentTaskStatus {
    OPEN,
    COMPLETED,
    CANCELLED
}
