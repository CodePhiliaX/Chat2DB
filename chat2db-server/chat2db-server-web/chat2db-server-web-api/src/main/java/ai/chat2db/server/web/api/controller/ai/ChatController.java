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
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.ai.azure.client.AzureOpenAIClient;
import ai.chat2db.server.web.api.controller.ai.listener.AzureOpenAIEventSourceListener;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ai.config.LocalCache;
import ai.chat2db.server.web.api.controller.ai.converter.ChatConverter;
import ai.chat2db.server.web.api.controller.ai.enums.GptVersionType;
import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import ai.chat2db.server.web.api.controller.ai.listener.OpenAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.listener.RestAIEventSourceListener;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.request.ChatRequest;
import ai.chat2db.server.web.api.controller.ai.rest.client.RestAIClient;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import ai.chat2db.server.web.api.util.OpenAIClient;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        SseEmitter sseEmitter = new SseEmitter(CHAT_TIMEOUT);
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BaseException(CommonError.SYS_ERROR);
        }
        return distributeAI(msg, sseEmitter, uid);
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
    private SseEmitter distributeAI(String msg, SseEmitter sseEmitter, String uid) throws IOException {
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
        switch (Objects.requireNonNull(aiSqlSourceEnum)) {
            case OPENAI :
            case CHAT2DBAI:
                return chatWithOpenAi(msg, sseEmitter, uid);
            case RESTAI :
                return chatWithRestAi(msg, sseEmitter);
        }
        return chatWithOpenAi(msg, sseEmitter, uid);
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
        switch (Objects.requireNonNull(aiSqlSourceEnum)) {
            case OPENAI :
            case CHAT2DBAI:
                return chatWithOpenAiSql(queryRequest, sseEmitter, uid);
            case RESTAI :
                return chatWithRestAi(queryRequest.getMessage(), sseEmitter);
            case AZUREAI :
                return chatWithAzureAi(queryRequest, sseEmitter, uid);
        }
        return chatWithOpenAiSql(queryRequest, sseEmitter, uid);
    }

    /**
     * 使用自定义AI接口进行聊天
     *
     * @param prompt
     * @param sseEmitter
     * @return
     */
    private SseEmitter chatWithRestAi(String prompt, SseEmitter sseEmitter) {
        RestAIEventSourceListener eventSourceListener = new RestAIEventSourceListener(sseEmitter);
        RestAIClient.getInstance().restCompletions(prompt, eventSourceListener);
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
            throw new ParamBusinessException();
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
        String messageContext = (String)LocalCache.CACHE.get(uid);
        List<ChatMessage> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, ChatMessage.class);
            if (messages.size() >= contextLength) {
                messages = messages.subList(1, contextLength);
            }
        }
        ChatMessage currentMessage = new ChatMessage(ChatRole.USER).setContent(prompt);
        messages.add(currentMessage);

        sseEmitter.send(SseEmitter.event().id(uid).name("sseEmitter connected！！！！").data(LocalDateTime.now()).reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            log.info(LocalDateTime.now() + ", uid#" + uid + ", sseEmitter on completion");
        });
        sseEmitter.onTimeout(
            () -> log.info(LocalDateTime.now() + ", uid#" + uid + ", sseEmitter on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(
            throwable -> {
                try {
                    log.info(LocalDateTime.now() + ", uid#" + "765431" + ", sseEmitter on error#" + throwable.toString());
                    sseEmitter.send(SseEmitter.event().id("765431").name("exception occurs！").data(throwable.getMessage())
                        .reconnectTime(3000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );
        AzureOpenAIEventSourceListener sourceListener = new AzureOpenAIEventSourceListener(sseEmitter);
        AzureOpenAIClient.getInstance().streamCompletions(messages, sourceListener);
        LocalCache.CACHE.put(uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        return sseEmitter;
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
        //if (I18nUtils.isEn()) {
        //    schemaProperty = String.format("%s\n#\n### 返回结果要求为英文", schemaProperty);
        //}
        return schemaProperty;
    }
}
