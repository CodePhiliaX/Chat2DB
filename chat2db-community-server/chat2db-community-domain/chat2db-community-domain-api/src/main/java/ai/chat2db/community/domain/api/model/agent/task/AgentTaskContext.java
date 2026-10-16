package ai.chat2db.community.domain.api.model.agent.task;

import java.util.List;
import java.util.Objects;

/** Resource references are resolved and authorized by the execution layer. */
public record AgentTaskContext(List<Scope> scopes, List<ObjectReference> objects,
                               List<String> attachmentIds) {
    public AgentTaskContext {
        scopes = scopes == null ? List.of() : List.copyOf(scopes);
        objects = objects == null ? List.of() : List.copyOf(objects);
        attachmentIds = attachmentIds == null ? List.of() : List.copyOf(attachmentIds);
        attachmentIds.forEach(id -> TaskModelChecks.requireText(id, "attachmentId"));
    }

    public static AgentTaskContext empty() {
        return new AgentTaskContext(List.of(), List.of(), List.of());
    }

    public record Scope(Long dataSourceId, String database, String schema) {
        public Scope {
            if (dataSourceId == null || dataSourceId < 1) {
                throw new IllegalArgumentException("dataSourceId must be positive");
            }
        }
    }

    public enum ObjectType { TABLE, VIEW }

    public record ObjectReference(Scope scope, ObjectType type, String name) {
        public ObjectReference {
            Objects.requireNonNull(scope, "scope");
            Objects.requireNonNull(type, "type");
            TaskModelChecks.requireText(name, "object name");
        }
    }
}
