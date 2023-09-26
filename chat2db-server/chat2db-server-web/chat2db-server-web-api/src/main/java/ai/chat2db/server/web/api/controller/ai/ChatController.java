package ai.chat2db.server.web.api.controller.ai;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ai.azure.client.AzureOpenAIClient;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatMessage;
import ai.chat2db.server.web.api.controller.ai.azure.model.AzureChatRole;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.ai.claude.client.ClaudeAIClient;
import ai.chat2db.server.web.api.controller.ai.claude.model.ClaudeChatCompletionsOptions;
import ai.chat2db.server.web.api.controller.ai.claude.model.ClaudeChatMessage;
import ai.chat2db.server.web.api.controller.ai.config.LocalCache;
import ai.chat2db.server.web.api.controller.ai.converter.ChatConverter;
import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import ai.chat2db.server.web.api.controller.ai.azure.listener.AzureOpenAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.claude.listener.ClaudeAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.openai.listener.OpenAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.rest.listener.RestAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.request.ChatRequest;
import ai.chat2db.server.web.api.controller.ai.rest.client.RestAIClient;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import ai.chat2db.server.web.api.controller.ai.openai.client.OpenAIClient;
import ai.chat2db.spi.model.TableColumn;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private static final Long CHAT_TIMEOUT = Duration.ofMinutes(50).toMillis();

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
     * 自定义模型流式输出接口DEMO
     * <p>
     *     Note:使用自己本地的流式输出的自定义AI，接口输入和输出需与该样例保持一致
     * </p>
     *
     * @param queryRequest
     * @return
     * @throws IOException
     */
    @PostMapping("/custom/stream/chat")
    @CrossOrigin
    public SseEmitter customChat(@RequestBody ChatRequest queryRequest) throws IOException {
        SseEmitter emitter = new SseEmitter(CHAT_TIMEOUT);

        // 设置 SSEEmitter 的事件处理程序
        emitter.onCompletion(() -> log.info(LocalDateTime.now() + ", on completion"));
        emitter.onTimeout(() -> {
            log.info(LocalDateTime.now() + ", uid# on timeout");
            emitter.complete();
        });

        // 启动一个新的线程来生成 SSE 事件
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send(SseEmitter.event().name("message").data("Event " + i));
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        }).start();

        return emitter;
    }

    /**
     * 自定义模型非流式输出接口DEMO
     * <p>
     *     Note:使用自己本地的飞流式输出自定义AI，接口输入和输出需与该样例保持一致
     * </p>
     *
     * @param queryRequest
     * @return
     * @throws IOException
     */
    @PostMapping("/custom/non/stream/chat")
    @CrossOrigin
    public String customNonStreamChat(@RequestBody ChatRequest queryRequest) throws IOException {
        String data = "自定义AI样例接口连接成功！！！！";
        return data;
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
            throw new ParamBusinessException("uid");
        }

        //提示消息不得为空
        if (StringUtils.isBlank(queryRequest.getMessage())) {
            throw new ParamBusinessException("message");
        }

        return distributeAISql(queryRequest, sseEmitter, uid);
    }

    /**
     * distribute with different AI
     *
     * @return
     */
    private SseEmitter distributeAISql(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid) throws IOException {
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config config = configService.find(RestAIClient.AI_SQL_SOURCE).getData();
        String aiSqlSource = AiSqlSourceEnum.CHAT2DBAI.getCode();
        if (Objects.nonNull(config)) {
            aiSqlSource = config.getContent();
        }
        AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(aiSqlSource);
        if (Objects.isNull(aiSqlSourceEnum)) {
            aiSqlSourceEnum = AiSqlSourceEnum.OPENAI;
        }
        uid = aiSqlSourceEnum.getCode() + uid;
        switch (Objects.requireNonNull(aiSqlSourceEnum)) {
            case OPENAI :
                return chatWithOpenAi(queryRequest, sseEmitter, uid);
            case CHAT2DBAI:
                return chatWithChat2dbAi(queryRequest, sseEmitter, uid);
            case RESTAI :
                return chatWithRestAi(queryRequest, sseEmitter);
            case AZUREAI :
                return chatWithAzureAi(queryRequest, sseEmitter, uid);
            case CLAUDEAI:
                return chatWithClaudeAi(queryRequest, sseEmitter, uid);
        }
        return chatWithOpenAi(queryRequest, sseEmitter, uid);
    }

    /**
     * 使用自定义AI接口进行聊天
     *
     * @param prompt
     * @param sseEmitter
     * @return
     */
    private SseEmitter chatWithRestAi(ChatQueryRequest prompt, SseEmitter sseEmitter) {
        RestAIEventSourceListener eventSourceListener = new RestAIEventSourceListener(sseEmitter);
        RestAIClient.getInstance().restCompletions(buildPrompt(prompt), eventSourceListener);
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
    private SseEmitter chatWithOpenAi(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid)
        throws IOException {
        String prompt = buildPrompt(queryRequest);
        if (prompt.length() / TOKEN_CONVERT_CHAR_LENGTH > MAX_PROMPT_LENGTH) {
            log.error("提示语超出最大长度:{}，输入长度:{}, 请重新输入", MAX_PROMPT_LENGTH,
                prompt.length() / TOKEN_CONVERT_CHAR_LENGTH);
            throw new ParamBusinessException();
        }

        List<Message> messages = new ArrayList<>();
        prompt = prompt.replaceAll("#", "");
        log.info(prompt);
        Message currentMessage = Message.builder().content(prompt).role(Message.Role.USER).build();
        messages.add(currentMessage);
        buildSseEmitter(sseEmitter, uid);

        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        OpenAIClient.getInstance().streamChatCompletion(messages, openAIEventSourceListener);
        LocalCache.CACHE.put(uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
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
    private SseEmitter chatWithChat2dbAi(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid)
        throws IOException {
        String prompt = buildPrompt(queryRequest);
        if (prompt.length() / TOKEN_CONVERT_CHAR_LENGTH > MAX_PROMPT_LENGTH) {
            log.error("exceed max token length:{}，input length:{}", MAX_PROMPT_LENGTH,
                prompt.length() / TOKEN_CONVERT_CHAR_LENGTH);
            throw new ParamBusinessException();
        }

        prompt = prompt.replaceAll("#", "");
        log.info(prompt);
        Message currentMessage = Message.builder().content(prompt).role(Message.Role.USER).build();
        List<Message> messages = new ArrayList<>();
        messages.add(currentMessage);
        buildSseEmitter(sseEmitter, uid);

        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        Chat2dbAIClient.getInstance().streamChatCompletion(messages, openAIEventSourceListener);
        LocalCache.CACHE.put(uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        return sseEmitter;
    }

    /**
     * chat with azure openai
     *
     * @param queryRequest
     * @param sseEmitter
     * @param uid
     * @return
     * @throws IOException
     */
    private SseEmitter chatWithAzureAi(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid) throws IOException {
        String prompt = buildPrompt(queryRequest);
        if (prompt.length() / TOKEN_CONVERT_CHAR_LENGTH > MAX_PROMPT_LENGTH) {
            log.error("提示语超出最大长度:{}，输入长度:{}, 请重新输入", MAX_PROMPT_LENGTH,
                    prompt.length() / TOKEN_CONVERT_CHAR_LENGTH);
            throw new ParamBusinessException();
        }
        List<AzureChatMessage> messages = (List<AzureChatMessage>)LocalCache.CACHE.get(uid);
        if (CollectionUtils.isNotEmpty(messages)) {
            if (messages.size() >= contextLength) {
                messages = messages.subList(1, contextLength);
            }
        } else {
            messages = Lists.newArrayList();
        }
        AzureChatMessage currentMessage = new AzureChatMessage(AzureChatRole.USER).setContent(prompt);
        messages.add(currentMessage);

        buildSseEmitter(sseEmitter, uid);

        AzureOpenAIEventSourceListener sourceListener = new AzureOpenAIEventSourceListener(sseEmitter);
        AzureOpenAIClient.getInstance().streamCompletions(messages, sourceListener);
        LocalCache.CACHE.put(uid, messages, LocalCache.TIMEOUT);
        return sseEmitter;
    }


    /**
     * chat with claude ai
     *
     * @param queryRequest
     * @param sseEmitter
     * @param uid
     * @return
     * @throws IOException
     */
    private SseEmitter chatWithClaudeAi(ChatQueryRequest queryRequest, SseEmitter sseEmitter, String uid) throws IOException {
        String prompt = buildPrompt(queryRequest);
        ClaudeChatMessage claudeChatMessage = new ClaudeChatMessage();
        claudeChatMessage.setText(prompt);
        ClaudeChatCompletionsOptions chatCompletionsOptions = new ClaudeChatCompletionsOptions();
        chatCompletionsOptions.setPrompt(prompt);
        claudeChatMessage.setCompletion(chatCompletionsOptions);

        buildSseEmitter(sseEmitter, uid);

        ClaudeAIEventSourceListener sourceListener = new ClaudeAIEventSourceListener(sseEmitter);
        ClaudeAIClient.getInstance().streamCompletions(claudeChatMessage, sourceListener);
        return sseEmitter;
    }

    /**
     * construct sseEmitter
     *
     * @param sseEmitter
     * @param uid
     * @return
     * @throws IOException
     */
    private SseEmitter buildSseEmitter(SseEmitter sseEmitter, String uid) throws IOException {
        sseEmitter.send(SseEmitter.event().id(uid).name("connect successfully！！！！").data(LocalDateTime.now()).reconnectTime(3000));
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
        List<TableColumn> tableColumns = Lists.newArrayList();
        try {
            tableColumns = tableService.queryColumns(tableQueryParam);
        } catch (Exception exception) {
            log.error("query table error, do nothing");
        }
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
        String dataSourceType = dataResult.getData().getType();
        if (StringUtils.isBlank(dataSourceType)) {
            dataSourceType = "MYSQL";
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

    ///**
    // * 问答对话模型
    // *
    // * @param msg
    // * @param headers
    // * @return
    // * @throws IOException
    // */
    //@GetMapping("/chat1")
    //@CrossOrigin
    //public SseEmitter chat(@RequestParam("message") String msg, @RequestHeader Map<String, String> headers)
    //    throws IOException {
    //    //默认30秒超时,设置为0L则永不超时
    //    SseEmitter sseEmitter = new SseEmitter(CHAT_TIMEOUT);
    //    String uid = headers.get("uid");
    //    if (StrUtil.isBlank(uid)) {
    //        throw new BaseException(CommonError.SYS_ERROR);
    //    }
    //    return distributeAI(msg, sseEmitter, uid);
    //}

    ///**
    // * distribute with different AI
    // *
    // * @return
    // */
    //private SseEmitter distributeAI(String msg, SseEmitter sseEmitter, String uid) throws IOException {
    //    ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
    //    Config config = configService.find(RestAIClient.AI_SQL_SOURCE).getData();
    //    String aiSqlSource = AiSqlSourceEnum.CHAT2DBAI.getCode();
    //    if (Objects.nonNull(config)) {
    //        aiSqlSource = config.getContent();
    //    }
    //    AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(aiSqlSource);
    //    if (Objects.isNull(aiSqlSourceEnum)) {
    //        aiSqlSourceEnum = AiSqlSourceEnum.OPENAI;
    //    }
    //    switch (Objects.requireNonNull(aiSqlSourceEnum)) {
    //        case OPENAI :
    //            return chatWithOpenAi(msg, sseEmitter, uid);
    //        case CHAT2DBAI:
    //            return chatWithOpenAi(msg, sseEmitter, uid);
    //        case RESTAI :
    //            return chatWithRestAi(msg, sseEmitter);
    //    }
    //    return chatWithOpenAi(msg, sseEmitter, uid);
    //}

    ///**
    // * 使用OPENAI聊天相关接口
    // *
    // * @param msg
    // * @param sseEmitter
    // * @param uid
    // * @return
    // * @throws IOException
    // */
    //private SseEmitter chatWithOpenAi(String msg, SseEmitter sseEmitter, String uid) throws IOException {
    //    String messageContext = (String)LocalCache.CACHE.get(uid);
    //    List<Message> messages = new ArrayList<>();
    //    if (StrUtil.isNotBlank(messageContext)) {
    //        messages = JSONUtil.toList(messageContext, Message.class);
    //        if (messages.size() >= contextLength) {
    //            messages = messages.subList(1, contextLength);
    //        }
    //        Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
    //        messages.add(currentMessage);
    //    } else {
    //        Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
    //        messages.add(currentMessage);
    //    }
    //
    //    return chatGpt35(messages, sseEmitter, uid);
    //}

    ///**
    // * 使用GPT3.0模型
    // *
    // * @param prompt
    // * @param sseEmitter
    // * @param uid
    // * @return
    // */
    //private SseEmitter chatGpt3(String prompt, SseEmitter sseEmitter, String uid) throws IOException {
    //    sseEmitter.send(SseEmitter.event().id(uid).name("chatGpt3连接成功！！！！").data(LocalDateTime.now())
    //        .reconnectTime(3000));
    //    sseEmitter.onCompletion(() -> {
    //        log.info(LocalDateTime.now() + ", uid#" + uid + ", on completion");
    //    });
    //    sseEmitter.onTimeout(
    //        () -> log.info(LocalDateTime.now() + ", uid#" + uid + ", chatGpt3 on timeout#" + sseEmitter.getTimeout()));
    //    sseEmitter.onError(
    //        throwable -> {
    //            try {
    //                log.info(LocalDateTime.now() + ", uid#" + "765431" + ", chatGpt3 on error#" + throwable.toString());
    //                sseEmitter.send(SseEmitter.event().id("765431").name("chatGpt3 发生异常！")
    //                    .data(throwable.getMessage())
    //                    .reconnectTime(3000));
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    );
    //
    //    // 获取返回结果
    //    OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
    //    Completion completion = Completion.builder().maxTokens(RETURN_TOKEN_LENGTH).stream(true).stop(
    //        Lists.newArrayList("#", ";")).user(uid).prompt(prompt).build();
    //    OpenAIClient.getInstance().streamCompletions(completion, openAIEventSourceListener);
    //    return sseEmitter;
    //}
}
