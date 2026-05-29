package ai.chat2db.server.web.api.controller.ai.statemachine.actions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSONObject;

import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatContext;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatEvent;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatState;
import ai.chat2db.server.web.api.controller.ai.statemachine.helper.SqlExplainHelper;
import ai.chat2db.server.web.api.controller.ai.statemachine.helper.ExplainResult;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 执行EXPLAIN动作
 * 仅在SQL_OPTIMIZER类型时执行，失败时静默降级
 */
@Component
@Slf4j
public class ExplainAction extends BaseChatAction {

    @Autowired
    private SqlExplainHelper sqlExplainHelper;

    private static final Pattern SQL_CODE_BLOCK_PATTERN = Pattern.compile("```sql\\s*([\\s\\S]+?)\\s*```", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_KEYWORD_PATTERN = Pattern.compile("(?i)\\b(SELECT|UPDATE|DELETE|INSERT|MERGE)\\b");

    @Override
    public void execute(StateContext<ChatState, ChatEvent> context) {
        log.info("[ExplainAction] execute called");
        ChatContext ctx = getChatContext(context);
        
        if (ctx.isCancelled()) {
            log.info("[ExplainAction] cancelled, returning");
            return;
        }

        ChatQueryRequest request = ctx.getRequest();
        PromptType promptType = determinePromptType(request);
        
        // 只对 SQL_OPTIMIZER 执行 EXPLAIN
        if (promptType != PromptType.SQL_OPTIMIZER) {
            log.info("[ExplainAction] Skip EXPLAIN for promptType: {}", promptType);
            triggerEvent(context, ChatEvent.EXPLAIN_NOT_NEEDED);
            return;
        }

        sendStateEvent(ctx.getSseEmitter(), ChatState.EXECUTING_EXPLAIN, "正在分析执行计划...");

        buildContext(ctx);
        try {
            String sql = extractSql(request.getMessage());
            if (sql == null) {
                log.warn("[ExplainAction] No SQL found in message");
                triggerEvent(context, ChatEvent.EXPLAIN_NOT_NEEDED);
                return;
            }

            log.info("[ExplainAction] Executing EXPLAIN for SQL: {}", sql);
            
            ExplainResult result = sqlExplainHelper.executeExplain(sql);
            
            if (result.isSuccess()) {
                ctx.setExplainSql(result.getExplainSql());
                ctx.setExplainResult(result.getFormattedPlan());
                
                sendExplainToClient(ctx.getSseEmitter(), result);
                log.info("[ExplainAction] EXPLAIN executed successfully");
                triggerEvent(context, ChatEvent.EXPLAIN_EXECUTED);
            } else {
                log.warn("[ExplainAction] EXPLAIN execution failed: {}", result.getErrorMessage());
                triggerEvent(context, ChatEvent.EXPLAIN_FAILED);
            }
        } catch (Exception e) {
            log.warn("[ExplainAction] EXPLAIN failed, will skip silently", e);
            triggerEvent(context, ChatEvent.EXPLAIN_FAILED);
        } finally {
            removeContext();
        }
    }

    private PromptType determinePromptType(ChatQueryRequest request) {
        String promptType = StringUtils.isBlank(request.getPromptType())
                ? PromptType.NL_2_SQL.getCode()
                : request.getPromptType();
        return PromptType.valueOf(promptType);
    }

    private String extractSql(String message) {
        if (StringUtils.isBlank(message)) {
            return null;
        }

        // 尝试从 markdown 代码块中提取 SQL
        Matcher matcher = SQL_CODE_BLOCK_PATTERN.matcher(message);
        if (matcher.find()) {
            String sql = matcher.group(1).trim();
            if (StringUtils.isNotBlank(sql)) {
                return sql;
            }
        }

        // 如果没有代码块，检查是否包含 SQL 关键字
        if (SQL_KEYWORD_PATTERN.matcher(message).find()) {
            return message.trim();
        }

        return null;
    }

    private void sendExplainToClient(SseEmitter emitter, ExplainResult result) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "explain");
            data.put("sql", result.getExplainSql());
            data.put("plan", result.getPlanRows());
            data.put("formatted", result.getFormattedPlan());
            data.put("success", result.isSuccess());

            emitter.send(SseEmitter.event()
                .name("explain")
                .data(data.toJSONString()));
        } catch (IOException e) {
            log.error("[ExplainAction] Failed to send explain result", e);
        }
    }

    private void triggerEvent(StateContext<ChatState, ChatEvent> context, ChatEvent event) {
        context.getStateMachine().sendEvent(
            Mono.just(MessageBuilder.withPayload(event).build())
        ).subscribe();
    }
}
