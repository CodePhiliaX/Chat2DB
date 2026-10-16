package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.service.agent.IAgentTaskDefinition;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public final class AgentTaskDefinitionRegistry {
    private final Map<AgentTaskType, IAgentTaskDefinition> definitions;

    public AgentTaskDefinitionRegistry(List<IAgentTaskDefinition> definitions) {
        Map<AgentTaskType, IAgentTaskDefinition> registered = new EnumMap<>(AgentTaskType.class);
        for (IAgentTaskDefinition definition : definitions) {
            if (registered.putIfAbsent(definition.type(), definition) != null) {
                throw new IllegalArgumentException("Duplicate task definition: " + definition.type());
            }
        }
        this.definitions = Map.copyOf(registered);
    }

    public IAgentTaskDefinition require(AgentTaskType type) {
        IAgentTaskDefinition definition = definitions.get(type);
        if (definition == null) throw new IllegalArgumentException("Unsupported task type: " + type);
        return definition;
    }
}
