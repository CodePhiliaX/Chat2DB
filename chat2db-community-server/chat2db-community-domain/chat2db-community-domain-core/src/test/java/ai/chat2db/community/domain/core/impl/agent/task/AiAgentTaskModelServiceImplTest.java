package ai.chat2db.community.domain.core.impl.agent.task;

import ai.chat2db.community.domain.api.enums.agent.AgentTaskCompletionMode;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskStatus;
import ai.chat2db.community.domain.api.enums.agent.AgentTaskType;
import ai.chat2db.community.domain.api.model.agent.task.AgentTask;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskContext;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDataSet;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskDelivery;
import ai.chat2db.community.domain.api.model.agent.task.IAgentTaskInput;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskPlan;
import ai.chat2db.community.domain.api.model.agent.task.AgentTaskResult;
import ai.chat2db.community.domain.api.model.request.agent.AiAgentTaskCreateRequest;
import ai.chat2db.community.domain.api.service.agent.IAiAgentTaskModelService;
import ai.chat2db.community.tools.util.CommunityIdentity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiAgentTaskModelServiceImplTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-30T04:00:00Z"), ZoneOffset.UTC);
    private static final AgentTask.Origin ORIGIN = new AgentTask.Origin("menu", "editor", null);
    private static final AgentTaskContext.Scope LEFT = new AgentTaskContext.Scope(1L, "db1", "public");
    private static final AgentTaskContext.Scope RIGHT = new AgentTaskContext.Scope(2L, "db2", null);
    private static final AgentTaskContext CONTEXT = new AgentTaskContext(List.of(LEFT, RIGHT),
            List.of(new AgentTaskContext.ObjectReference(LEFT, AgentTaskContext.ObjectType.TABLE, "orders"),
                    new AgentTaskContext.ObjectReference(RIGHT, AgentTaskContext.ObjectType.VIEW, "orders")),
            List.of("attachment"));
    private final AtomicInteger ids = new AtomicInteger();
    private final IAiAgentTaskModelService service = new AiAgentTaskModelServiceImpl(registry(),
            () -> "id-" + ids.incrementAndGet(), CLOCK);

    @ParameterizedTest
    @MethodSource("taskCases")
    void concreteDefinitionsPrepareAndDeliverTypedResults(AgentTaskType type, IAgentTaskInput input,
                                                          Class<?> contentType, String evidence) {
        AgentTask task = create(type, input);
        AgentTaskPlan plan = service.prepare(task);
        AgentTaskDelivery delivery = service.deliver(task, plan, "run", "model response");

        assertEquals(type, plan.type());
        assertEquals(input, plan.inputSnapshot());
        assertEquals(CONTEXT, plan.inputSnapshot().context());
        assertTrue(plan.prompt().contains(evidence));
        assertInstanceOf(contentType, delivery.result().content());
        assertEquals(task.id(), delivery.result().taskId());
        assertEquals("run", delivery.result().runId());
        assertEquals(1, delivery.result().taskRevision());
        assertEquals(LocalDateTime.now(CLOCK), delivery.result().gmtCreate());
        assertEquals(type == AgentTaskType.CHAT ? AgentTaskStatus.OPEN : AgentTaskStatus.COMPLETED,
                delivery.task().status());
        assertEquals(type == AgentTaskType.CHAT ? AgentTaskCompletionMode.CONTINUOUS
                : AgentTaskCompletionMode.ON_DELIVERY, plan.completionMode());
    }

    @Test
    void chatFollowUpsRetainTaskAndConversationIdentity() {
        AgentTask first = create(AgentTaskType.CHAT, text("first question"));
        AgentTaskPlan firstPlan = service.prepare(first);
        AgentTaskDelivery answered = service.deliver(first, firstPlan, "run-1", "first answer");
        AgentTask second = service.advance(answered.task(), text("follow up"));
        AgentTaskDelivery secondAnswer = service.deliver(second, service.prepare(second),
                "run-2", "second answer");

        assertEquals(first.id(), second.id());
        assertEquals(first.userId(), second.userId());
        assertEquals(first.type(), second.type());
        assertEquals(first.sessionId(), second.sessionId());
        assertEquals(first.origin(), second.origin());
        assertEquals(first.title(), second.title());
        assertEquals(first.gmtCreate(), second.gmtCreate());
        assertEquals(2, second.revision());
        assertEquals(AgentTaskStatus.OPEN, secondAnswer.task().status());
        assertFalse(answered.result().id().equals(secondAnswer.result().id()));
        assertEquals(1, first.revision());
        assertEquals("first question", first.input().instruction());
        assertThrows(IllegalArgumentException.class,
                () -> service.deliver(second, firstPlan, "late-run", "late response"));
    }

    @Test
    void sqlProposalPreservesTheEditorBaseline() {
        IAgentTaskInput.Sql input = sql("use limit", "syntax error");
        AgentTask task = create(AgentTaskType.SQL_REPAIR, input);
        AgentTaskPlan plan = service.prepare(task);
        AgentTaskDelivery delivery = service.deliver(task, plan, "run", "  SELECT 1  ");
        AgentTaskResult.SqlChange change = assertInstanceOf(
                AgentTaskResult.SqlChange.class, delivery.result().content());

        assertEquals(input.selection(), change.selection());
        assertEquals(input.sql(), change.originalSql());
        assertEquals("SELECT 1", change.proposedSql());
        assertTrue(plan.prompt().contains(input.error()));
        assertTrue(plan.prompt().contains(input.instruction()));
        assertEquals(AgentTaskStatus.OPEN, task.status());
        assertNull(task.sessionId());
    }

    @Test
    void comparisonIdentifiesSnapshotsAcrossSourcesEvenWithDuplicateLabels() {
        IAgentTaskInput.Data input = data();
        AgentTask task = create(AgentTaskType.DATA_COMPARISON, input);
        AgentTaskPlan plan = service.prepare(task);
        AgentTaskResult.Comparison result = assertInstanceOf(AgentTaskResult.Comparison.class,
                service.deliver(task, plan, "run", "comparison").result().content());

        assertEquals("left", result.leftDataSetId());
        assertEquals("right", result.rightDataSetId());
        assertEquals(LEFT, ((IAgentTaskInput.Data) plan.inputSnapshot()).dataSets().get(0).source());
        assertEquals(RIGHT, ((IAgentTaskInput.Data) plan.inputSnapshot()).dataSets().get(1).source());
        assertTrue(plan.prompt().contains(input.dataSets().get(0).contentJson()));
        assertTrue(plan.prompt().contains(input.dataSets().get(1).contentJson()));
        assertTrue(plan.prompt().contains(input.instruction()));
    }

    @Test
    void analysisAcceptsUploadedDataWithoutDatasource() {
        IAgentTaskInput.Data input = new IAgentTaskInput.Data("analyze", List.of(
                new AgentTaskDataSet("upload", "file", "{\"total\":1.25,\"note\":null}", null)),
                AgentTaskContext.empty(), null);
        AgentTask task = create(AgentTaskType.DATA_ANALYSIS, input);
        AgentTaskResult.Analysis result = assertInstanceOf(AgentTaskResult.Analysis.class,
                service.deliver(task, service.prepare(task), "run", "report").result().content());
        assertEquals(List.of("upload"), result.dataSetIds());
    }

    @Test
    void deliveredTaskCanBeRefinedButCannotDeliverAgainWithoutAdvancing() {
        AgentTask task = create(AgentTaskType.SQL_REWRITE, sql("use limit", ""));
        AgentTaskPlan plan = service.prepare(task);
        AgentTask completed = service.deliver(task, plan, "run", "SELECT 1").task();

        assertThrows(IllegalStateException.class,
                () -> service.deliver(completed, plan, "other-run", "SELECT 2"));
        assertThrows(IllegalArgumentException.class, () -> service.prepare(completed));
        assertSame(completed, service.complete(completed));
        AgentTask refined = service.advance(completed, sql("sort descending", ""));
        assertEquals(task.id(), refined.id());
        assertEquals(2, refined.revision());
        assertEquals(AgentTaskStatus.OPEN, refined.status());
        assertThrows(IllegalArgumentException.class,
                () -> service.deliver(refined, plan, "late-run", "SELECT 1"));
    }

    @Test
    void cancellingAndExplicitlyClosingAChatAreBusinessTransitions() {
        AgentTask task = create(AgentTaskType.CHAT, text("question"));
        AgentTaskPlan plan = service.prepare(task);
        AgentTask closed = service.complete(task);
        assertEquals(AgentTaskStatus.COMPLETED, closed.status());
        assertSame(closed, service.complete(closed));
        assertEquals(AgentTaskStatus.OPEN, service.advance(closed, text("continue")).status());

        AgentTask cancelled = service.cancel(task);
        assertEquals(AgentTaskStatus.CANCELLED, cancelled.status());
        assertSame(cancelled, service.cancel(cancelled));
        assertThrows(IllegalStateException.class, () -> service.complete(cancelled));
        assertThrows(IllegalStateException.class, () -> service.advance(cancelled, text("continue")));
        assertThrows(IllegalArgumentException.class, () -> service.prepare(cancelled));
        assertThrows(IllegalStateException.class,
                () -> service.deliver(cancelled, plan, "run", "answer"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"task", "owner", "type", "revision", "input", "completion"})
    void deliveryRejectsPlansForADifferentTaskSnapshot(String mismatch) {
        AgentTask task = create(AgentTaskType.CHAT, text("question"));
        AgentTaskPlan plan = service.prepare(task);
        AgentTaskPlan invalid = new AgentTaskPlan(
                mismatch.equals("task") ? "another-task" : plan.taskId(),
                mismatch.equals("owner") ? 2L : plan.userId(),
                mismatch.equals("type") ? AgentTaskType.SQL_REPAIR : plan.type(),
                mismatch.equals("revision") ? 2 : plan.taskRevision(),
                mismatch.equals("input") ? text("changed") : plan.inputSnapshot(),
                plan.prompt(), mismatch.equals("completion") ? AgentTaskCompletionMode.ON_DELIVERY
                        : plan.completionMode());
        assertThrows(IllegalArgumentException.class,
                () -> service.deliver(task, invalid, "run", "answer"));
    }

    @ParameterizedTest
    @EnumSource(AgentTaskType.class)
    void definitionsRejectWrongInputTypes(AgentTaskType type) {
        IAgentTaskInput wrong = type == AgentTaskType.CHAT ? sql("rewrite", "error") : text("question");
        assertThrows(IllegalArgumentException.class, () -> create(type, wrong));
        assertThrows(IllegalArgumentException.class, () -> registry().require(type).materialize(wrong, "answer"));
    }

    @Test
    void sqlTasksRequireTheirSpecificMenuArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> create(AgentTaskType.SQL_REPAIR, sql("", " ")));
        assertThrows(IllegalArgumentException.class,
                () -> create(AgentTaskType.SQL_REWRITE, sql(" ", "error")));
        assertEquals(AgentTaskType.SQL_REPAIR, create(AgentTaskType.SQL_REPAIR, sql("", "error")).type());
        assertEquals(AgentTaskType.SQL_REWRITE, create(AgentTaskType.SQL_REWRITE, sql("rewrite", "")).type());
    }

    @ParameterizedTest
    @ValueSource(strings = {"[", "[] {}", "null", "42", "\"text\""})
    void dataTasksRejectInvalidOrNonStructuredJson(String json) {
        IAgentTaskInput.Data invalid = new IAgentTaskInput.Data("analyze", List.of(
                new AgentTaskDataSet("data", "label", json, LEFT)), CONTEXT, null);
        assertThrows(IllegalArgumentException.class, () -> create(AgentTaskType.DATA_ANALYSIS, invalid));
    }

    @Test
    void dataTasksEnforceCardinalityAndStableDistinctIds() {
        assertThrows(IllegalArgumentException.class, () -> create(AgentTaskType.DATA_ANALYSIS,
                new IAgentTaskInput.Data("analyze", List.of(), CONTEXT, null)));
        AgentTaskDataSet left = data().dataSets().get(0);
        AgentTaskDataSet right = data().dataSets().get(1);
        for (List<AgentTaskDataSet> sets : List.of(List.<AgentTaskDataSet>of(), List.of(left),
                List.of(left, right, new AgentTaskDataSet("third", "orders", "[]", null)),
                List.of(left, left))) {
            assertThrows(IllegalArgumentException.class, () -> create(AgentTaskType.DATA_COMPARISON,
                    new IAgentTaskInput.Data("compare", sets, CONTEXT, null)));
        }
        assertThrows(IllegalArgumentException.class, () -> create(AgentTaskType.DATA_ANALYSIS,
                new IAgentTaskInput.Data("analyze", List.of(left, left), CONTEXT, null)));
    }

    @Test
    void deliveryRequiresANonEmptyResponseAndRunIdentity() {
        AgentTask task = create(AgentTaskType.CHAT, text("question"));
        AgentTaskPlan plan = service.prepare(task);
        for (String response : new String[] {null, "", "  "}) {
            assertThrows(IllegalArgumentException.class, () -> service.deliver(task, plan, "run", response));
        }
        assertThrows(IllegalArgumentException.class, () -> service.deliver(task, plan, " ", "answer"));
    }

    @Test
    void registryRejectsDuplicateAndMissingDefinitions() {
        assertThrows(IllegalArgumentException.class, () -> new AgentTaskDefinitionRegistry(
                List.of(new ChatTaskDefinition(), new ChatTaskDefinition())));
        assertThrows(IllegalArgumentException.class,
                () -> new AgentTaskDefinitionRegistry(List.of()).require(AgentTaskType.CHAT));
    }

    @Test
    void springWiresAllDefinitionsAndTheModelServiceWithoutARuntime() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(ObjectMapper.class, () -> new ObjectMapper());
            context.register(ChatTaskDefinition.class, SqlRepairTaskDefinition.class,
                    SqlRewriteTaskDefinition.class, DataAnalysisTaskDefinition.class,
                    DataComparisonTaskDefinition.class, AgentTaskDefinitionRegistry.class,
                    AiAgentTaskModelServiceImpl.class);
            context.refresh();
            AgentTaskDefinitionRegistry registry = context.getBean(AgentTaskDefinitionRegistry.class);
            for (AgentTaskType type : AgentTaskType.values()) {
                assertEquals(type, registry.require(type).type());
            }
            IAiAgentTaskModelService wired = context.getBean(IAiAgentTaskModelService.class);
            assertEquals(AgentTaskStatus.OPEN, wired.create(new AiAgentTaskCreateRequest(
                    1L, AgentTaskType.CHAT, "question", "session", text("question"), ORIGIN)).status());
        }
    }

    @Test
    void callerPromptReplacesTheDefinitionDefaultForThatRevision() {
        AgentTask defaults = create(AgentTaskType.SQL_REPAIR, sql("", "syntax error"));
        assertTrue(service.prepare(defaults).prompt().contains("Repair the selected SQL"));

        AgentTask custom = create(AgentTaskType.SQL_REPAIR,
                sql("", "syntax error", "house rules only"));
        AgentTaskPlan plan = service.prepare(custom);
        assertEquals("house rules only", plan.prompt());
        assertEquals("house rules only", plan.inputSnapshot().promptOverride());
        assertInstanceOf(AgentTaskResult.SqlChange.class,
                service.deliver(custom, plan, "run", "select 1").result().content());

        AgentTask advanced = service.advance(custom, sql("", "syntax error", "second prompt"));
        assertEquals(2, advanced.revision());
        assertEquals("second prompt", service.prepare(advanced).prompt());
    }

    @Test
    void promptOverrideIsPartOfTheInputSnapshot() {
        AgentTask task = create(AgentTaskType.CHAT, text("question", "caller prompt"));
        AgentTaskPlan plan = service.prepare(task);
        assertEquals("caller prompt", plan.prompt());

        AgentTaskPlan withoutOverride = new AgentTaskPlan(plan.taskId(), plan.userId(), plan.type(),
                plan.taskRevision(), text("question"), plan.prompt(), plan.completionMode());
        assertThrows(IllegalArgumentException.class,
                () -> service.deliver(task, withoutOverride, "run", "answer"));
    }

    @Test
    void communityLocalUserOwnsTasksWithTheNegativeIdentity() {
        AgentTask task = service.create(new AiAgentTaskCreateRequest(CommunityIdentity.USER_ID,
                AgentTaskType.CHAT, "question", "session", text("question"), ORIGIN));

        AgentTaskPlan plan = service.prepare(task);

        assertEquals(CommunityIdentity.USER_ID, task.userId().longValue());
        assertEquals(CommunityIdentity.USER_ID, plan.userId().longValue());
    }

    private AgentTask create(AgentTaskType type, IAgentTaskInput input) {
        return service.create(new AiAgentTaskCreateRequest(1L, type, "title",
                type == AgentTaskType.CHAT ? "session" : null, input, ORIGIN));
    }

    private static AgentTaskDefinitionRegistry registry() {
        ObjectMapper mapper = new ObjectMapper();
        return new AgentTaskDefinitionRegistry(List.of(new ChatTaskDefinition(),
                new SqlRepairTaskDefinition(), new SqlRewriteTaskDefinition(),
                new DataAnalysisTaskDefinition(mapper), new DataComparisonTaskDefinition(mapper)));
    }

    private static IAgentTaskInput.Text text(String instruction) {
        return text(instruction, null);
    }

    private static IAgentTaskInput.Text text(String instruction, String promptOverride) {
        return new IAgentTaskInput.Text(instruction, CONTEXT, promptOverride);
    }

    private static IAgentTaskInput.Sql sql(String instruction, String error) {
        return sql(instruction, error, null);
    }

    private static IAgentTaskInput.Sql sql(String instruction, String error, String promptOverride) {
        return new IAgentTaskInput.Sql(instruction, "select 1", error,
                new IAgentTaskInput.SqlSelection("editor", 7, 10, 18), CONTEXT, promptOverride);
    }

    private static IAgentTaskInput.Data data() {
        return new IAgentTaskInput.Data("compare totals", List.of(
                new AgentTaskDataSet("left", "orders", "[{\"total\":1.25,\"note\":null}]", LEFT),
                new AgentTaskDataSet("right", "orders", "[{\"total\":2.5,\"note\":\"test\"}]", RIGHT)),
                CONTEXT, null);
    }

    private static Stream<Arguments> taskCases() {
        return Stream.of(
                Arguments.of(AgentTaskType.CHAT, text("question"), AgentTaskResult.Message.class, "question"),
                Arguments.of(AgentTaskType.SQL_REPAIR, sql("", "syntax error"),
                        AgentTaskResult.SqlChange.class, "syntax error"),
                Arguments.of(AgentTaskType.SQL_REWRITE, sql("use limit", ""),
                        AgentTaskResult.SqlChange.class, "use limit"),
                Arguments.of(AgentTaskType.DATA_ANALYSIS, data(), AgentTaskResult.Analysis.class, "1.25"),
                Arguments.of(AgentTaskType.DATA_COMPARISON, data(), AgentTaskResult.Comparison.class, "2.5"));
    }
}
