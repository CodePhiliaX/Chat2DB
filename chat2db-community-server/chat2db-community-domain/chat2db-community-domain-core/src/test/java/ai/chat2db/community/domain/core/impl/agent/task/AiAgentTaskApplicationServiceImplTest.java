package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskContext;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskApplicationService;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskStorage;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiAgentTaskApplicationServiceImplTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-30T04:00:00Z"), ZoneOffset.UTC);
    private static final AgentTask.Origin ORIGIN = new AgentTask.Origin("menu", "editor", null);
    private static final AgentTaskContext CONTEXT = AgentTaskContext.empty();

    private final AtomicInteger identifiers = new AtomicInteger();
    private final InMemoryStorage storage = new InMemoryStorage();
    private final IAiAgentTaskApplicationService service = new AiAgentTaskApplicationServiceImpl(
            new AiAgentTaskModelServiceImpl(registry(),
                    () -> "task-value-" + identifiers.incrementAndGet(), CLOCK), storage);

    @Test
    void coordinatesCreatePrepareAdvanceAndDeliveryThroughStorage() {
        AgentTask created = service.create(request(7L, "first question"));
        assertEquals(created, storage.tasks.get(created.id()));

        AgentTaskPlan firstPlan = service.prepare(created.id(), 7L);
        AgentTask advanced = service.advance(created.id(), 7L, text("follow up"));
        assertEquals(2, advanced.revision());
        assertEquals(advanced, service.get(created.id(), 7L));

        AgentTaskDelivery delivery = service.deliver(created.id(), 7L,
                service.prepare(created.id(), 7L), "run-1", "answer");
        assertTrue(storage.deliverySaved);
        assertEquals(2, delivery.result().taskRevision());
        assertEquals(List.of(delivery.result()), service.listResults(created.id(), 7L));
        assertEquals(AgentTaskStatus.OPEN, service.get(created.id(), 7L).status());
        assertEquals(1, firstPlan.taskRevision());
    }

    @Test
    void rejectsStaleRevisionAndDoesNotChangeTheStoredTask() {
        AgentTask created = service.create(request(7L, "question"));
        AgentTaskPlan stalePlan = service.prepare(created.id(), 7L);
        AgentTask advanced = service.advance(created.id(), 7L, text("new question"));

        assertThrows(IllegalArgumentException.class,
                () -> service.deliver(created.id(), 7L, stalePlan, "late-run", "late answer"));
        assertEquals(advanced, storage.tasks.get(created.id()));

        storage.failNextCompareAndSet = true;
        assertThrows(IllegalStateException.class,
                () -> service.advance(created.id(), 7L, text("conflicting question")));
        assertEquals(advanced, storage.tasks.get(created.id()));
    }

    @Test
    void keepsUserIsolationAcrossTaskAndResultOperations() {
        AgentTask created = service.create(request(7L, "private question"));

        assertThrows(IllegalArgumentException.class, () -> service.get(created.id(), 8L));
        assertThrows(IllegalArgumentException.class, () -> service.prepare(created.id(), 8L));
        assertThrows(IllegalArgumentException.class, () -> service.listResults(created.id(), 8L));
        assertTrue(service.list(8L).isEmpty());
        assertEquals(List.of(created), service.list(7L));
    }

    @Test
    void persistsExplicitCompleteAndCancelTransitionsWithCas() {
        AgentTask complete = service.create(request(7L, "complete me"));
        AgentTask completed = service.complete(complete.id(), 7L);
        assertEquals(AgentTaskStatus.COMPLETED, completed.status());
        assertEquals(complete.revision(), completed.revision());

        AgentTask cancel = service.create(request(7L, "cancel me"));
        AgentTask cancelled = service.cancel(cancel.id(), 7L);
        assertEquals(AgentTaskStatus.CANCELLED, cancelled.status());
        assertEquals(cancelled, storage.tasks.get(cancel.id()));

        storage.failNextCompareAndSet = true;
        assertThrows(IllegalStateException.class, () -> service.complete(cancel.id(), 7L));
    }

    private AiAgentTaskCreateRequest request(Long userId, String instruction) {
        return new AiAgentTaskCreateRequest(userId, AgentTaskType.CHAT, "task", "session-" + userId,
                text(instruction), ORIGIN);
    }

    private static IAgentTaskInput.Text text(String instruction) {
        return new IAgentTaskInput.Text(instruction, CONTEXT, null);
    }

    private static AgentTaskDefinitionRegistry registry() {
        return new AgentTaskDefinitionRegistry(List.of(new ChatTaskDefinition(),
                new SqlRepairTaskDefinition(), new SqlRewriteTaskDefinition(),
                new DataAnalysisTaskDefinition(new com.fasterxml.jackson.databind.ObjectMapper()),
                new DataComparisonTaskDefinition(new com.fasterxml.jackson.databind.ObjectMapper())));
    }

    private static final class InMemoryStorage implements IAiAgentTaskStorage {
        private final Map<String, AgentTask> tasks = new LinkedHashMap<>();
        private final Map<String, List<AgentTaskResult>> results = new LinkedHashMap<>();
        private boolean failNextCompareAndSet;
        private boolean deliverySaved;

        @Override
        public AgentTask create(AgentTask task) {
            if (tasks.putIfAbsent(task.id(), task) != null) {
                throw new IllegalStateException("duplicate task");
            }
            return task;
        }

        @Override
        public AgentTask get(String taskId, Long userId) {
            AgentTask task = tasks.get(taskId);
            return task != null && Objects.equals(task.userId(), userId) ? task : null;
        }

        @Override
        public List<AgentTask> list(Long userId) {
            return tasks.values().stream().filter(task -> Objects.equals(task.userId(), userId)).toList();
        }

        @Override
        public boolean compareAndSet(AgentTask task, AgentTaskStatus expectedStatus,
                                     long expectedRevision, Long userId) {
            if (failNextCompareAndSet) {
                failNextCompareAndSet = false;
                return false;
            }
            AgentTask current = get(task.id(), userId);
            if (current == null || current.status() != expectedStatus
                    || current.revision() != expectedRevision) {
                return false;
            }
            tasks.put(task.id(), task);
            return true;
        }

        @Override
        public AgentTaskDelivery saveDelivery(AgentTaskDelivery delivery, AgentTaskStatus expectedStatus,
                                              long expectedRevision, Long userId) {
            AgentTask current = get(delivery.task().id(), userId);
            if (current == null || current.status() != expectedStatus
                    || current.revision() != expectedRevision) {
                throw new IllegalStateException("stale delivery");
            }
            tasks.put(delivery.task().id(), delivery.task());
            results.computeIfAbsent(delivery.task().id(), ignored -> new ArrayList<>()).add(delivery.result());
            deliverySaved = true;
            return delivery;
        }

        @Override
        public AgentTaskResult createResult(AgentTaskResult result, Long userId) {
            if (get(result.taskId(), userId) == null) {
                throw new IllegalArgumentException("missing task");
            }
            results.computeIfAbsent(result.taskId(), ignored -> new ArrayList<>()).add(result);
            return result;
        }

        @Override
        public List<AgentTaskResult> listResults(String taskId, Long userId) {
            if (get(taskId, userId) == null) {
                return List.of();
            }
            return List.copyOf(results.getOrDefault(taskId, List.of()));
        }
    }
}
