package ai.chat2db.community.storage.agent;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskStorage;
import ai.chat2db.community.storage.StorageFileUtils;
import ai.chat2db.community.tools.exception.storage.StorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Local JSON storage for task snapshots. Runtime sessions remain in their existing V2 storage. */
@Component
public class AiAgentTaskStorageImpl implements IAiAgentTaskStorage {
    private static final int STORAGE_SCHEMA_VERSION = 2;

    private final AgentV2StoragePaths paths;
    private final StorageFileUtils files;
    private final ObjectMapper mapper;

    @Autowired
    public AiAgentTaskStorageImpl(AgentV2StoragePaths paths, StorageFileUtils files, ObjectMapper mapper) {
        this.paths = Objects.requireNonNull(paths, "paths");
        this.files = Objects.requireNonNull(files, "files");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public synchronized AgentTask create(AgentTask task) {
        validateTask(task);
        ensureStorage();
        Path directory = paths.taskDirectory(task.id());
        Path file = paths.taskFile(task.id());
        files.rejectSymbolicLink(directory);
        files.rejectSymbolicLink(file);
        if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            throw new StorageException("Agent task already exists: " + task.id());
        }
        files.createPrivateDirectory(directory);
        files.verifyInsideRoot(paths.root(), directory);
        try {
            write(file, task);
        } catch (RuntimeException exception) {
            files.deleteEmptyDirectory(directory);
            throw exception;
        }
        return task;
    }

    @Override
    public synchronized AgentTask get(String taskId, Long userId) {
        Objects.requireNonNull(userId, "userId");
        Path file = paths.taskFile(taskId);
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }
        validateExistingStorage();
        AgentTask task = readTask(file);
        return Objects.equals(userId, task.userId()) ? task : null;
    }

    @Override
    public synchronized List<AgentTask> list(Long userId) {
        Objects.requireNonNull(userId, "userId");
        Path directory = paths.tasksDirectory();
        if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }
        validateExistingStorage();
        files.rejectSymbolicLink(directory);
        try (Stream<Path> entries = Files.list(directory)) {
            return entries
                    .peek(files::rejectSymbolicLink)
                    .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                    .map(path -> readTask(path.resolve("task.json")))
                    .filter(task -> Objects.equals(userId, task.userId()))
                    .sorted(Comparator.comparing(AgentTask::gmtModified).reversed()
                            .thenComparing(AgentTask::id))
                    .toList();
        } catch (IOException exception) {
            throw new StorageException("Failed to list agent tasks", exception);
        }
    }

    @Override
    public synchronized boolean compareAndSet(
            AgentTask task, AgentTaskStatus expectedStatus, long expectedRevision, Long userId) {
        validateTask(task);
        Objects.requireNonNull(expectedStatus, "expectedStatus");
        Objects.requireNonNull(userId, "userId");
        if (!Objects.equals(userId, task.userId())) {
            throw new StorageException("Agent task owner cannot be changed: " + task.id());
        }
        Path file = paths.taskFile(task.id());
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            return false;
        }
        validateExistingStorage();
        AgentTask existing = readTask(file);
        if (!Objects.equals(existing.userId(), userId)) {
            throw new StorageException("Agent task is not owned by the user: " + task.id());
        }
        if (existing.status() != expectedStatus || existing.revision() != expectedRevision) {
            return false;
        }
        validateIdentity(existing, task);
        if (task.revision() < expectedRevision || task.revision() > expectedRevision + 1) {
            throw new IllegalArgumentException("Task revision must stay unchanged or advance by one");
        }
        write(file, task);
        return true;
    }

    @Override
    public synchronized AgentTaskDelivery saveDelivery(
            AgentTaskDelivery delivery, AgentTaskStatus expectedStatus, long expectedRevision, Long userId) {
        Objects.requireNonNull(delivery, "delivery");
        Objects.requireNonNull(expectedStatus, "expectedStatus");
        Objects.requireNonNull(userId, "userId");
        AgentTask current = get(delivery.task().id(), userId);
        if (current == null) {
            throw new StorageException("Agent task does not exist or is not owned by the user");
        }
        if (current.status() != expectedStatus || current.revision() != expectedRevision) {
            throw new IllegalStateException("Agent task changed while delivering: " + current.id());
        }
        validateTask(delivery.task());
        validateIdentity(current, delivery.task());
        if (delivery.result().taskRevision() != expectedRevision) {
            throw new IllegalArgumentException("Agent task result revision does not match the expected task");
        }
        Path resultDirectory = paths.taskResultsDirectory(current.id());
        Path resultFile = paths.taskResultFile(current.id(), delivery.result().id());
        files.createPrivateDirectory(resultDirectory);
        files.verifyInsideRoot(paths.root(), resultDirectory);
        files.rejectSymbolicLink(resultFile);
        if (Files.exists(resultFile, LinkOption.NOFOLLOW_LINKS)) {
            AgentTaskResult existingResult = readResult(resultFile, current.id());
            if (existingResult.equals(delivery.result()) && current.equals(delivery.task())) {
                return delivery;
            }
            throw new StorageException("Agent task result already exists: " + delivery.result().id());
        }
        boolean resultWritten = false;
        try {
            write(resultFile, delivery.result());
            resultWritten = true;
            write(paths.taskFile(current.id()), delivery.task());
            return delivery;
        } catch (RuntimeException exception) {
            if (resultWritten) {
                deleteResultAfterFailedDelivery(resultFile, exception);
            }
            throw exception;
        }
    }

    @Override
    public synchronized AgentTaskResult createResult(AgentTaskResult result, Long userId) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(userId, "userId");
        AgentTask task = get(result.taskId(), userId);
        if (task == null) {
            throw new StorageException("Agent task does not exist or is not owned by the user");
        }
        Path directory = paths.taskResultsDirectory(task.id());
        Path file = paths.taskResultFile(task.id(), result.id());
        files.createPrivateDirectory(directory);
        files.verifyInsideRoot(paths.root(), directory);
        files.rejectSymbolicLink(file);
        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new StorageException("Agent task result already exists: " + result.id());
        }
        write(file, result);
        return result;
    }

    @Override
    public synchronized List<AgentTaskResult> listResults(String taskId, Long userId) {
        AgentTask task = get(taskId, userId);
        if (task == null) {
            return List.of();
        }
        Path directory = paths.taskResultsDirectory(taskId);
        if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }
        files.rejectSymbolicLink(directory);
        try (Stream<Path> entries = Files.list(directory)) {
            return entries
                    .peek(files::rejectSymbolicLink)
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(path -> readResult(path, taskId))
                    .toList();
        } catch (IOException exception) {
            throw new StorageException("Failed to list agent task results", exception);
        }
    }

    private void ensureStorage() {
        files.createPrivateDirectory(paths.root());
        files.rejectSymbolicLink(paths.root());
        Path schema = paths.schemaFile();
        if (Files.exists(schema, LinkOption.NOFOLLOW_LINKS)) {
            validateExistingStorage();
        } else {
            files.writeAtomically(schema, "{\"schemaVersion\":" + STORAGE_SCHEMA_VERSION + "}");
        }
        files.createPrivateDirectory(paths.tasksDirectory());
        files.rejectSymbolicLink(paths.tasksDirectory());
        files.verifyInsideRoot(paths.root(), paths.tasksDirectory());
    }

    private void validateExistingStorage() {
        files.rejectSymbolicLink(paths.root());
        Path schema = paths.schemaFile();
        files.rejectSymbolicLink(schema);
        if (!Files.isRegularFile(schema, LinkOption.NOFOLLOW_LINKS)) {
            throw new StorageException("Missing V2 agent storage schema");
        }
        try {
            String content = Files.readString(schema, StandardCharsets.UTF_8);
            JsonNode document = mapper.readTree(content);
            if (document == null || document.path("schemaVersion").asInt(-1) != STORAGE_SCHEMA_VERSION) {
                throw new StorageException("Unsupported V2 agent storage schema");
            }
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof StorageException storageException) {
                throw storageException;
            }
            throw new StorageException("Failed to read V2 agent storage schema", exception);
        }
    }

    private AgentTask readTask(Path file) {
        files.rejectSymbolicLink(file.getParent());
        files.rejectSymbolicLink(file);
        files.verifyInsideRoot(paths.root(), file);
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new StorageException("Missing agent task file: " + file.getFileName());
        }
        try {
            AgentTask task = mapper.readValue(Files.readString(file, StandardCharsets.UTF_8), AgentTask.class);
            validateTask(task);
            return task;
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof StorageException storageException) {
                throw storageException;
            }
            throw new StorageException("Failed to read agent task", exception);
        }
    }

    private AgentTaskResult readResult(Path file, String taskId) {
        files.rejectSymbolicLink(file);
        files.verifyInsideRoot(paths.root(), file);
        try {
            AgentTaskResult result = mapper.readValue(
                    Files.readString(file, StandardCharsets.UTF_8), AgentTaskResult.class);
            if (!Objects.equals(taskId, result.taskId())) {
                throw new StorageException("Agent task result belongs to another task");
            }
            return result;
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof StorageException storageException) {
                throw storageException;
            }
            throw new StorageException("Failed to read agent task result", exception);
        }
    }

    private void write(Path file, Object value) {
        try {
            files.writeAtomically(file, mapper.writeValueAsString(value));
        } catch (JsonProcessingException exception) {
            throw new StorageException("Failed to serialize agent task storage record", exception);
        }
    }

    private void deleteResultAfterFailedDelivery(Path resultFile, RuntimeException original) {
        try {
            Files.deleteIfExists(resultFile);
        } catch (IOException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }

    private void validateTask(AgentTask task) {
        Objects.requireNonNull(task, "task");
        if (task.gmtModified().isBefore(task.gmtCreate())) {
            throw new IllegalArgumentException("Agent task timestamps are invalid");
        }
        paths.taskFile(task.id());
    }

    private void validateIdentity(AgentTask existing, AgentTask next) {
        if (!Objects.equals(existing.id(), next.id())
                || !Objects.equals(existing.userId(), next.userId())
                || existing.type() != next.type()
                || !Objects.equals(existing.sessionId(), next.sessionId())
                || !Objects.equals(existing.origin(), next.origin())
                || !Objects.equals(existing.gmtCreate(), next.gmtCreate())) {
            throw new IllegalArgumentException("Agent task identity cannot be changed");
        }
    }
}
