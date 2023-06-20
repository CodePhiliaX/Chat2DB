package com.alibaba.dbhub.server.web.api.controller.ai;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.dbhub.server.domain.api.enums.AiSqlSourceEnum;
import com.alibaba.dbhub.server.domain.api.model.Config;
import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.TableQueryParam;
import com.alibaba.dbhub.server.domain.api.service.ConfigService;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.api.service.TableService;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.TableColumn;
import com.alibaba.dbhub.server.tools.base.excption.BusinessException;
import com.alibaba.dbhub.server.tools.base.excption.CommonErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.common.util.EasyEnumUtils;
import com.alibaba.dbhub.server.web.api.aspect.ConnectionInfoAspect;
import com.alibaba.dbhub.server.web.api.controller.ai.config.LocalCache;
import com.alibaba.dbhub.server.web.api.controller.ai.converter.ChatConverter;
import com.alibaba.dbhub.server.web.api.controller.ai.enums.GptVersionType;
import com.alibaba.dbhub.server.web.api.controller.ai.enums.PromptType;
import com.alibaba.dbhub.server.web.api.controller.ai.listener.OpenAIEventSourceListener;
import com.alibaba.dbhub.server.web.api.controller.ai.request.ChatQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.ai.rest.client.RestAIClient;
import com.alibaba.dbhub.server.web.api.util.ApplicationContextUtil;
import com.alibaba.dbhub.server.web.api.util.OpenAIClient;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@RestController
@ConnectionInfoAspect
@RequestMapping("/api/ai")
@Slf4j
public class ChatController {

    @Autowired
    private TableService tableService;

    @Autowired
    private ChatConverter chatConverter;

    @Autowired
    private DataSourceService dataSourceService;

    @Value("${chatgpt.context.length}")
    private Integer contextLength;

    @Value("${chatgpt.version}")
    private String gptVersion;

    /**
     * chat的超时时间
     */
    private static final Long CHAT_TIMEOUT = Duration.ofMinutes(10).toMillis();

    /**
     * 提示语最大token数
     */
    private Integer MAX_PROMPT_LENGTH = 3850;

    /**
     * token转换字符串长度
     */
    private Integer TOKEN_CONVERT_CHAR_LENGTH = 4;

    /**
     * 返回token大小
     */
    private Integer RETURN_TOKEN_LENGTH = 150;

    /**
     * 问答对话模型
     *
     * @param msg
     * @param headers
     * @return
     * @throws IOException
     */
    @GetMapping("/chat1")
    @CrossOrigin
    public SseEmitter chat(@RequestParam("message") String msg, @RequestHeader Map<String, String> headers)
        throws IOException {
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BaseException(CommonError.SYS_ERROR);
        }
        if (useOpenAI()) {
            return chatWithOpenAi(msg, sseEmitter, uid);
        }
        return chatWithRestAi(msg, sseEmitter);
    }

    /**
     * SQL转换模型
     *
     * @param queryRequest
     * @param headers
     * @return
     * @throws IOException
     */
    @GetMapping("/chat")
    @CrossOrigin
    public SseEmitter completions(ChatQueryRequest queryRequest, @RequestHeader Map<String, String> headers)
        throws IOException {
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(CHAT_TIMEOUT);
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BusinessException(CommonErrorEnum.COMMON_SYSTEM_ERROR);
        }

        //提示消息不得为空
        if (StringUtils.isBlank(queryRequest.getMessage())) {
            throw new BusinessException(CommonErrorEnum.PARAM_ERROR);
        }

        if (useOpenAI()) {
            return chatWithOpenAiSql(queryRequest, sseEmitter, uid);
        }
        return chatWithRestAi(queryRequest.getMessage(), sseEmitter);
    }

    /**
     * 是否使用OPENAI
     *
     * @return
     */
    private Boolean useOpenAI() {
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config config = configService.find(RestAIClient.REST_AI_URL).getData();
        if (Objects.nonNull(config) && AiSqlSourceEnum.RESTAI.getCode().equals(config.getContent())) {
            return false;
        }
        return true;
    }

    /**
     * 使用自定义AI接口进行聊天
     *
     * @param prompt
     * @param sseEmitter
     * @return
     */
    private SseEmitter chatWithRestAi(String prompt, SseEmitter sseEmitter) {
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        RestAIClient.getInstance().restCompletions(prompt, openAIEventSourceListener, sseEmitter);
        return sseEmitter;
    }

    /**
     * 使用OPENAI SQL接口
     *
     * @param queryRequest
     * @param sseEmitter
     * @param uid
     * @return
     * @throws IOException
     */
    private SseEmitter chatWithOpenAiSql(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid)
        throws IOException {
        String prompt = buildPrompt(queryRequest);
        if (prompt.length() / TOKEN_CONVERT_CHAR_LENGTH > MAX_PROMPT_LENGTH) {
            log.error("提示语超出最大长度:{}，输入长度:{}, 请重新输入", MAX_PROMPT_LENGTH,
                prompt.length() / TOKEN_CONVERT_CHAR_LENGTH);
            throw new BusinessException(CommonErrorEnum.PARAM_ERROR);
        }

        GptVersionType modelType = EasyEnumUtils.getEnum(GptVersionType.class, gptVersion);
        switch (modelType) {
            case GPT3:
                return chatGpt3(prompt, sseEmitter, uid);
            case GPT35:
                List<Message> messages = new ArrayList<>();
                prompt = prompt.replaceAll("#", "");
                log.info(prompt);
                Message currentMessage = Message.builder().content(prompt).role(Message.Role.USER).build();
                messages.add(currentMessage);
                return chatGpt35(messages, sseEmitter, uid);
            default:
                break;
        }
        return chatGpt3(prompt, sseEmitter, uid);
    }

    /**
     * 使用OPENAI聊天相关接口
     *
     * @param msg
     * @param sseEmitter
     * @param uid
     * @return
     * @throws IOException
     */
    private SseEmitter chatWithOpenAi(String msg, SseEmitter sseEmitter, String uid) throws IOException {
        String messageContext = (String)LocalCache.CACHE.get(uid);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (messages.size() >= contextLength) {
                messages = messages.subList(1, contextLength);
            }
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        }

        return chatGpt35(messages, sseEmitter, uid);
    }

    /**
     * 使用GPT3.5模型
     *
     * @param messages
     * @param sseEmitter
     * @param uid
     * @return
     */
    private SseEmitter chatGpt35(List<Message> messages, SseEmitter sseEmitter, String uid) throws IOException {
        sseEmitter.send(SseEmitter.event().id(uid).name("连接成功！！！！").data(LocalDateTime.now()).reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            log.info(LocalDateTime.now() + ", uid#" + uid + ", on completion");
        });
        sseEmitter.onTimeout(
            () -> log.info(LocalDateTime.now() + ", uid#" + uid + ", on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(
            throwable -> {
                try {
                    log.info(LocalDateTime.now() + ", uid#" + "765431" + ", on error#" + throwable.toString());
                    sseEmitter.send(SseEmitter.event().id("765431").name("发生异常！").data(throwable.getMessage())
                        .reconnectTime(3000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        OpenAIClient.getInstance().streamChatCompletion(messages, openAIEventSourceListener);
        LocalCache.CACHE.put(uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        return sseEmitter;
    }

    /**
     * 使用GPT3.0模型
     *
     * @param prompt
     * @param sseEmitter
     * @param uid
     * @return
     */
    private SseEmitter chatGpt3(String prompt, SseEmitter sseEmitter, String uid) throws IOException {
        sseEmitter.send(SseEmitter.event().id(uid).name("chatGpt3连接成功！！！！").data(LocalDateTime.now())
            .reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            log.info(LocalDateTime.now() + ", uid#" + uid + ", on completion");
        });
        sseEmitter.onTimeout(
            () -> log.info(LocalDateTime.now() + ", uid#" + uid + ", chatGpt3 on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(
            throwable -> {
                try {
                    log.info(LocalDateTime.now() + ", uid#" + "765431" + ", chatGpt3 on error#" + throwable.toString());
                    sseEmitter.send(SseEmitter.event().id("765431").name("chatGpt3 发生异常！")
                        .data(throwable.getMessage())
                        .reconnectTime(3000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        // 获取返回结果
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        Completion completion = Completion.builder().maxTokens(RETURN_TOKEN_LENGTH).stream(true).stop(
            Lists.newArrayList("#", ";")).user(uid).prompt(prompt).build();
        OpenAIClient.getInstance().streamCompletions(completion, openAIEventSourceListener);
        return sseEmitter;
    }

    /**
     * 构建schema参数
     *
     * @param tableQueryParam
     * @param tableNames
     * @return
     */
    private Map<String, List<TableColumn>> buildTableColumn(TableQueryParam tableQueryParam,
        List<String> tableNames) {
        if (CollectionUtils.isEmpty(tableNames)) {
            return Maps.newHashMap();
        }
        List<TableColumn> tableColumns = tableService.queryColumns(tableQueryParam);
        if (CollectionUtils.isEmpty(tableColumns)) {
            return Maps.newHashMap();
        }
        return tableColumns.stream().filter(tableColumn -> tableNames.contains(tableColumn.getTableName())).collect(
            Collectors.groupingBy(TableColumn::getTableName, Collectors.toList()));
    }

    /**
     * 构建prompt
     *
     * @param queryRequest
     * @return
     */
    private String buildPrompt(ChatQueryRequest queryRequest) {
        // 查询schema信息
        DataResult<DataSource> dataResult = dataSourceService.queryById(queryRequest.getDataSourceId());
        String dataSourceType = DbTypeEnum.MYSQL.getCode();
        if (StringUtils.isNotBlank(dataSourceType)) {
            dataSourceType = dataResult.getData().getType();
        }
        TableQueryParam queryParam = chatConverter.chat2tableQuery(queryRequest);
        Map<String, List<TableColumn>> tableColumns = buildTableColumn(queryParam, queryRequest.getTableNames());
        List<String> tableSchemas = tableColumns.entrySet().stream().map(
            entry -> String.format("%s(%s)", entry.getKey(),
                entry.getValue().stream().map(TableColumn::getName).collect(
                    Collectors.joining(", ")))).collect(Collectors.toList());
        String properties = String.join("\n#", tableSchemas);
        String prompt = queryRequest.getMessage();
        String promptType = StringUtils.isBlank(queryRequest.getPromptType()) ? PromptType.NL_2_SQL.getCode()
            : queryRequest.getPromptType();
        PromptType pType = EasyEnumUtils.getEnum(PromptType.class, promptType);
        String ext = StringUtils.isNotBlank(queryRequest.getExt()) ? queryRequest.getExt() : "";
        String schemaProperty = CollectionUtils.isNotEmpty(tableSchemas) ? String.format(
            "### 请根据以下table properties和SQL input%s. %s\n#\n### %s SQL tables, with their properties:\n#\n# "
                + "%s\n#\n#\n### SQL input: %s", pType.getDescription(), ext, dataSourceType,
            properties, prompt) : String.format("### 请根据以下SQL input%s. %s\n#\n### SQL input: %s",
            pType.getDescription(), ext, prompt);
        switch (pType) {
            case SQL_2_SQL:
                schemaProperty = StringUtils.isNotBlank(queryRequest.getDestSqlType()) ? String.format(
                    "%s\n#\n### 目标SQL类型: %s", schemaProperty, queryRequest.getDestSqlType()) : String.format(
                    "%s\n#\n### 目标SQL类型: %s", schemaProperty, dataSourceType);
            default:
                break;
        }
        return schemaProperty;
    }
}
