package ai.chat2db.community.storage.agent;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskContext;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ai.chat2db.community.storage.StorageFileUtils;
import ai.chat2db.community.tools.exception.storage.StorageException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiAgentTaskStorageImplTest {
    private static final Long OWNER = 7L;
    private static final String TASK_ID = "task-one";
    private static final LocalDateTime CREATED = LocalDateTime.of(2026, 10, 8, 11, 0);

    @TempDir
    Path temporaryDirectory;

    private AgentV2StoragePaths paths;
    private AiAgentTaskStorageImpl storage;

    @BeforeEach
    void setUp() {
        paths = new AgentV2StoragePaths(temporaryDirectory.resolve("storage/ai-chat-history-v2"));
        storage = new AiAgentTaskStorageImpl(paths, new StorageFileUtils(), mapper());
    }

    @Test
    void createsListsAndReloadsTasksWithoutRuntimeClassNames() throws Exception {
        AgentTask task = task(AgentTaskStatus.OPEN, 1, text("question"));

        assertEquals(task, storage.create(task));
        assertEquals(task, storage.get(TASK_ID, OWNER));
        assertNull(storage.get(TASK_ID, 8L));
        assertTrue(storage.list(8L).isEmpty());
        assertEquals(List.of(task), storage.list(OWNER));
        assertTrue(Files.isRegularFile(paths.taskFile(TASK_ID)));
        assertFalse(Files.readString(paths.taskFile(TASK_ID)).contains("ai.chat2db"));

        AiAgentTaskStorageImpl reloaded = new AiAgentTaskStorageImpl(
                paths, new StorageFileUtils(), mapper());
        assertEquals(task, reloaded.get(TASK_ID, OWNER));
        assertEquals(List.of(task), reloaded.list(OWNER));
    }

    @Test
    void compareAndSetProtectsOwnerIdentityStatusAndRevision() {
        AgentTask task = storage.create(task(AgentTaskStatus.OPEN, 1, text("question")));
        AgentTask completed = task(AgentTaskStatus.COMPLETED, 1, text("question"));

        assertTrue(storage.compareAndSet(completed, AgentTaskStatus.OPEN, 1, OWNER));
        assertEquals(completed, storage.get(TASK_ID, OWNER));
        assertFalse(storage.compareAndSet(task, AgentTaskStatus.OPEN, 1, OWNER));
        assertFalse(storage.compareAndSet(completed, AgentTaskStatus.OPEN, 1, OWNER));

        AgentTask advanced = task(AgentTaskStatus.OPEN, 2, text("follow up"));
        assertTrue(storage.compareAndSet(advanced, AgentTaskStatus.COMPLETED, 1, OWNER));
        assertEquals(advanced, storage.get(TASK_ID, OWNER));

        AgentTask changedIdentity = new AgentTask(
                TASK_ID, OWNER, AgentTaskType.SQL_REWRITE, "title", AgentTaskStatus.OPEN,
                null, new IAgentTaskInput.Sql("rewrite", "select 1", "",
                new IAgentTaskInput.SqlSelection("editor", 1, 0, 8), AgentTaskContext.empty(), null),
                task.origin(), 3, CREATED, CREATED.plusMinutes(1));
        assertThrows(IllegalArgumentException.class,
                () -> storage.compareAndSet(changedIdentity, AgentTaskStatus.OPEN, 2, OWNER));
        assertThrows(StorageException.class,
                () -> storage.compareAndSet(advanced, AgentTaskStatus.OPEN, 2, 8L));
    }

    @Test
    void storesPolymorphicResultsAndKeepsResultHistoryAcrossReloads() {
        AgentTask task = storage.create(task(AgentTaskStatus.OPEN, 1, text("question")));
        AgentTaskResult result = new AgentTaskResult(
                "result-one", TASK_ID, "run-one", 1,
                new AgentTaskResult.Message("answer"), CREATED.plusMinutes(1));

        assertEquals(result, storage.createResult(result, OWNER));
        assertEquals(List.of(result), storage.listResults(TASK_ID, OWNER));
        assertTrue(Files.isRegularFile(paths.taskResultFile(TASK_ID, result.id())));
        assertTrue(storage.listResults(TASK_ID, 8L).isEmpty());
        assertThrows(StorageException.class, () -> storage.createResult(result, OWNER));

        AiAgentTaskStorageImpl reloaded = new AiAgentTaskStorageImpl(
                paths, new StorageFileUtils(), mapper());
        assertEquals(List.of(result), reloaded.listResults(TASK_ID, OWNER));
        assertEquals(task, reloaded.get(TASK_ID, OWNER));
    }

    @Test
    void savesDeliveryAsTaskTransitionAndResultSnapshot() {
        AgentTask task = storage.create(task(AgentTaskStatus.OPEN, 1, text("question")));
        AgentTask completed = task(AgentTaskStatus.COMPLETED, 1, text("question"));
        AgentTaskResult result = new AgentTaskResult(
                "result-delivery", TASK_ID, "run-delivery", 1,
                new AgentTaskResult.Message("answer"), CREATED.plusMinutes(1));
        AgentTaskDelivery delivery = new AgentTaskDelivery(completed, result);

        assertEquals(delivery, storage.saveDelivery(delivery, AgentTaskStatus.OPEN, 1, OWNER));
        assertEquals(completed, storage.get(TASK_ID, OWNER));
        assertEquals(List.of(result), storage.listResults(TASK_ID, OWNER));
        assertThrows(IllegalStateException.class,
                () -> storage.saveDelivery(delivery, AgentTaskStatus.OPEN, 1, OWNER));
    }

    @Test
    void rejectsMalformedTaskStorageAndInvalidRevisionTransitions() throws Exception {
        AgentTask task = storage.create(task(AgentTaskStatus.OPEN, 1, text("question")));
        assertThrows(StorageException.class, () -> storage.create(task));

        AgentTask revisionJump = task(AgentTaskStatus.OPEN, 3, text("jump"));
        assertThrows(IllegalArgumentException.class,
                () -> storage.compareAndSet(revisionJump, AgentTaskStatus.OPEN, 1, OWNER));

        Files.writeString(paths.taskFile(TASK_ID), "{not-json");
        AiAgentTaskStorageImpl reloaded = new AiAgentTaskStorageImpl(
                paths, new StorageFileUtils(), mapper());
        assertThrows(StorageException.class, () -> reloaded.get(TASK_ID, OWNER));
    }

    private AgentTask task(AgentTaskStatus status, long revision, IAgentTaskInput input) {
        return new AgentTask(
                TASK_ID, OWNER, AgentTaskType.CHAT, "title", status, "session-one", input,
                new AgentTask.Origin("chat", "panel", null), revision, CREATED,
                CREATED.plusMinutes(revision));
    }

    private static IAgentTaskInput.Text text(String instruction) {
        return new IAgentTaskInput.Text(instruction, AgentTaskContext.empty(), null);
    }

    private static ObjectMapper mapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
