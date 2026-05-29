package ai.chat2db.server.web.api.controller.ai.statemachine.actions;

import java.util.concurrent.CompletableFuture;

import ai.chat2db.server.domain.api.service.DataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import ai.chat2db.server.web.api.controller.ai.prompt.PromptBuilder;
import ai.chat2db.server.web.api.controller.ai.prompt.PromptContext;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatContext;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatEvent;
import ai.chat2db.server.web.api.controller.ai.statemachine.ChatState;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 构建提示词动作
 */
@Component
@Slf4j
public class BuildPromptAction extends BaseChatAction {

    @Autowired
    private PromptBuilder promptBuilder;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public void execute(StateContext<ChatState, ChatEvent> context) {
        log.info("[BuildPromptAction] execute called");
        ChatContext chatContext = getChatContext(context);
        log.info("[BuildPromptAction] uid: {}, cancelled: {}", chatContext.getUid(), chatContext.isCancelled());
        if (chatContext.isCancelled()) {
            log.info("[BuildPromptAction] cancelled, returning");
            return;
        }

        sendStateEvent(chatContext.getSseEmitter(),
                ChatState.BUILDING_PROMPT, "正在构建提示...");

        buildContext(chatContext);
        try {
            ChatQueryRequest request = chatContext.getRequest();
            String schemaDdl = chatContext.getSchemaDdl();
            log.info("[BuildPromptAction] Building prompt for uid: {}, promptType: {}, message: {}",
                    chatContext.getUid(), request.getPromptType(), request.getMessage());

            PromptType promptType = determinePromptType(request);

            // 解析 ext 中的 sourceFields（用于字段映射）
            String sourceFields = extractSourceFields(request.getExt());

            PromptContext promptContext = PromptContext.builder()
                    .promptType(promptType)
                    .message(request.getMessage())
                    .ext(request.getExt())
                    .schemaDdl(schemaDdl)
                    .explainPlan(chatContext.getExplainResult())
                    .dataSourceType(dataSourceService.queryDatabaseType(request.getDataSourceId()))
                    .targetSqlType(request.getDestSqlType())
                    .sourceFields(sourceFields)
                    .build();

            String builtPrompt = promptBuilder.context(promptContext).build();
            log.info("[BuildPromptAction] Built prompt content for uid: {}:\n{}", chatContext.getUid(), builtPrompt);
            chatContext.setBuiltPrompt(builtPrompt);

            log.info("[BuildPromptAction] Sending PROMPT_BUILT event for uid: {}", chatContext.getUid());
            context.getStateMachine().sendEvent(
                    Mono.just(MessageBuilder.withPayload(ChatEvent.PROMPT_BUILT).build())
            ).subscribe();
        } catch (Exception e) {
            log.error("[BuildPromptAction] Build prompt failed for uid: {}", chatContext.getUid(), e);
            sendError(chatContext.getSseEmitter(),
                    "构建提示失败：" + e.getMessage());
            context.getStateMachine().sendEvent(
                    Mono.just(MessageBuilder.withPayload(ChatEvent.PROMPT_BUILD_FAILED).build())
            ).subscribe();
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

    private String extractSourceFields(String ext) {
        if (StringUtils.isBlank(ext)) {
            return null;
        }
        try {
            com.alibaba.fastjson2.JSONObject extJson = com.alibaba.fastjson2.JSON.parseObject(ext);
            if (extJson.containsKey("sourceFields")) {
                return extJson.getString("sourceFields");
            }
        } catch (Exception e) {
            log.warn("[BuildPromptAction] Failed to parse sourceFields from ext", e);
        }
        return null;
    }
}
