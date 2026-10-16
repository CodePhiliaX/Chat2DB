package ai.chat2db.community.domain.api.model.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentTaskContractTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 30, 12, 0);
    private static final AgentTaskContext.Scope SOURCE = new AgentTaskContext.Scope(1L, "db", "public");
    private static final IAgentTaskInput.SqlSelection SELECTION =
            new IAgentTaskInput.SqlSelection("editor", 3, 10, 18);

    @ParameterizedTest
    @MethodSource("inputs")
    void inputsAndPlansRoundTripWithTypeDiscriminators(IAgentTaskInput input) throws Exception {
        String json = mapper.writerFor(IAgentTaskInput.class).writeValueAsString(input);
        assertTrue(json.contains("\"kind\""));
        assertEquals(input, mapper.readValue(json, IAgentTaskInput.class));

        AgentTaskPlan plan = new AgentTaskPlan("task", 1L, typeOf(input), 2,
                input, "prompt", AgentTaskCompletionMode.ON_DELIVERY);
        assertEquals(plan, mapper.readValue(mapper.writeValueAsString(plan), AgentTaskPlan.class));
    }

    @ParameterizedTest
    @MethodSource("contents")
    void resultsRoundTripWithTypeDiscriminatorsAndTimestamps(AgentTaskResult.IContent content)
            throws Exception {
        AgentTaskResult result = new AgentTaskResult("result", "task", "run", 2, content, NOW);
        String json = mapper.writeValueAsString(result);
        assertTrue(json.contains("\"kind\""));
        assertEquals(result, mapper.readValue(json, AgentTaskResult.class));
    }

    @Test
    void taskAndDeliveryRoundTrip() throws Exception {
        AgentTask task = task("session", 1, new IAgentTaskInput.Text("question", context(), null));
        AgentTaskResult result = new AgentTaskResult("result", task.id(), "run", task.revision(),
                new AgentTaskResult.Message("answer"), NOW);
        AgentTaskDelivery delivery = new AgentTaskDelivery(task, result);

        assertEquals(task, mapper.readValue(mapper.writeValueAsString(task), AgentTask.class));
        assertEquals(delivery,
                mapper.readValue(mapper.writeValueAsString(delivery), AgentTaskDelivery.class));
    }

    @Test
    void collectionsAreImmutableSnapshots() {
        List<AgentTaskContext.Scope> scopes = new ArrayList<>(List.of(SOURCE));
        List<AgentTaskContext.ObjectReference> objects = new ArrayList<>(List.of(
                new AgentTaskContext.ObjectReference(SOURCE, AgentTaskContext.ObjectType.TABLE, "orders")));
        List<String> attachments = new ArrayList<>(List.of("file"));
        AgentTaskContext context = new AgentTaskContext(scopes, objects, attachments);
        List<AgentTaskDataSet> sets = new ArrayList<>(List.of(dataSet()));
        IAgentTaskInput.Data input = new IAgentTaskInput.Data("analyze", sets, context, null);
        List<String> ids = new ArrayList<>(List.of("set"));
        AgentTaskResult.Analysis analysis = new AgentTaskResult.Analysis(ids, "report");

        scopes.clear();
        objects.clear();
        attachments.clear();
        sets.clear();
        ids.clear();

        assertEquals(context(), context);
        assertEquals(List.of(dataSet()), input.dataSets());
        assertEquals(List.of("set"), analysis.dataSetIds());
        assertThrows(UnsupportedOperationException.class, () -> context.scopes().clear());
        assertThrows(UnsupportedOperationException.class, () -> context.objects().clear());
        assertThrows(UnsupportedOperationException.class, () -> context.attachmentIds().clear());
        assertThrows(UnsupportedOperationException.class, () -> input.dataSets().clear());
        assertThrows(UnsupportedOperationException.class, () -> analysis.dataSetIds().clear());
    }

    @Test
    void chatRequiresLogicalSessionAndPositiveInputRevision() {
        IAgentTaskInput.Text input = new IAgentTaskInput.Text("question", AgentTaskContext.empty(), null);
        assertThrows(IllegalArgumentException.class, () -> task(null, 1, input));
        assertThrows(IllegalArgumentException.class, () -> task(" ", 1, input));
        assertThrows(IllegalArgumentException.class, () -> task("session", 0, input));
        assertEquals(AgentTaskContext.empty(), new AgentTaskContext(null, null, null));
    }

    @Test
    void sqlSelectionUsesUtf16OffsetsAndPreservesVersion() {
        String sql = "select '😀'";
        IAgentTaskInput.SqlSelection selection = new IAgentTaskInput.SqlSelection(
                "editor", 7, 20, 20 + sql.length());
        IAgentTaskInput.Sql input = new IAgentTaskInput.Sql(null, sql, null, selection,
                AgentTaskContext.empty(), null);

        assertEquals("", input.instruction());
        assertEquals("", input.error());
        assertEquals(7, input.selection().documentVersion());
        assertThrows(IllegalArgumentException.class, () -> new IAgentTaskInput.Sql(
                "rewrite", sql, "", new IAgentTaskInput.SqlSelection("editor", 7, 20, 29),
                AgentTaskContext.empty(), null));
        assertThrows(IllegalArgumentException.class,
                () -> new IAgentTaskInput.SqlSelection("editor", -1, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new AgentTaskResult.SqlChange(selection, "select 1", "select 2"));
    }

    @Test
    void deliveryRejectsResultForAnotherTaskOrInputRevision() {
        AgentTask task = task("session", 2, new IAgentTaskInput.Text("question", context(), null));
        assertThrows(IllegalArgumentException.class, () -> new AgentTaskDelivery(task,
                new AgentTaskResult("result", "another-task", "run", 2,
                        new AgentTaskResult.Message("answer"), NOW)));
        assertThrows(IllegalArgumentException.class, () -> new AgentTaskDelivery(task,
                new AgentTaskResult("result", task.id(), "run", 1,
                        new AgentTaskResult.Message("answer"), NOW)));
    }

    private static Stream<IAgentTaskInput> inputs() {
        return Stream.of(new IAgentTaskInput.Text("question", context(), "caller prompt"),
                new IAgentTaskInput.Sql("rewrite", "select 1", "syntax", SELECTION, context(), null),
                new IAgentTaskInput.Data("analyze", List.of(dataSet()), context(), null));
    }

    @Test
    void blankPromptOverrideIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new IAgentTaskInput.Text("question", AgentTaskContext.empty(), " "));
        assertThrows(IllegalArgumentException.class,
                () -> new IAgentTaskInput.Sql("rewrite", "select 1", "", SELECTION, context(), ""));
        assertThrows(IllegalArgumentException.class,
                () -> new IAgentTaskInput.Data("analyze", List.of(dataSet()), context(), "  "));
    }

    private static Stream<AgentTaskResult.IContent> contents() {
        return Stream.of(new AgentTaskResult.Message("answer"),
                new AgentTaskResult.SqlChange(SELECTION, "select 1", "select 2"),
                new AgentTaskResult.Analysis(List.of("set"), "report"),
                new AgentTaskResult.Comparison("left", "right", "comparison"));
    }

    private static AgentTaskDataSet dataSet() {
        return new AgentTaskDataSet("set", "orders", "[{\"total\":1.25,\"note\":null}]", SOURCE);
    }

    private static AgentTaskContext context() {
        return new AgentTaskContext(List.of(SOURCE), List.of(new AgentTaskContext.ObjectReference(
                SOURCE, AgentTaskContext.ObjectType.TABLE, "orders")), List.of("file"));
    }

    private static AgentTaskType typeOf(IAgentTaskInput input) {
        if (input instanceof IAgentTaskInput.Text) return AgentTaskType.CHAT;
        if (input instanceof IAgentTaskInput.Sql) return AgentTaskType.SQL_REWRITE;
        return AgentTaskType.DATA_ANALYSIS;
    }

    private static AgentTask task(String sessionId, long revision, IAgentTaskInput input) {
        return new AgentTask("task", 1L, AgentTaskType.CHAT, "title", AgentTaskStatus.OPEN,
                sessionId, input, new AgentTask.Origin("chat", "panel", null), revision, NOW, NOW);
    }
}
