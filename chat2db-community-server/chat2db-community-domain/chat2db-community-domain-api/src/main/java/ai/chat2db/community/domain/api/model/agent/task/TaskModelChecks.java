package ai.chat2db.community.domain.api.model.agent.task;

final class TaskModelChecks {
    private TaskModelChecks() {}

    static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    static void optionalText(String value, String name) {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank when present");
        }
    }
}
