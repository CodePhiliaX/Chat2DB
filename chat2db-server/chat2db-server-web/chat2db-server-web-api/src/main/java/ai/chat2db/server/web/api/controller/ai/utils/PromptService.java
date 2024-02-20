package ai.chat2db.server.web.api.controller.ai.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.unfbx.chatgpt.entity.chat.Parameters;
import com.unfbx.chatgpt.entity.chat.tool.ToolsFunction;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.tools.base.enums.WhiteListTypeEnum;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.ai.config.LocalCache;
import ai.chat2db.server.web.api.controller.ai.converter.ChatConverter;
import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import ai.chat2db.server.web.api.controller.ai.fastchat.client.FastChatAIClient;
import ai.chat2db.server.web.api.controller.ai.fastchat.embeddings.FastChatEmbeddingResponse;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import ai.chat2db.server.web.api.controller.ai.fastchat.model.FastChatRole;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.ai.rest.client.RestAIClient;
import ai.chat2db.server.web.api.http.GatewayClientService;
import ai.chat2db.server.web.api.http.model.TableSchema;
import ai.chat2db.server.web.api.http.request.TableSchemaRequest;
import ai.chat2db.server.web.api.http.request.WhiteListRequest;
import ai.chat2db.server.web.api.http.response.TableSchemaResponse;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.sql.Chat2DBContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ConnectionInfoAspect
@Service
public class PromptService {


    @Value("${chatgpt.context.length}")
    private Integer contextLength;


    @Autowired
    private TableService tableService;

    @Autowired
    private DataSourceService dataSourceService;


    @Autowired
    private ChatConverter chatConverter;


    @Resource
    private GatewayClientService gatewayClientService;


    /**
     * 构建prompt
     *
     * @param queryRequest
     * @return
     */
    public String buildPrompt(ChatQueryRequest queryRequest) {
        if (PromptType.TEXT_GENERATION.getCode().equals(queryRequest.getPromptType())) {
            return queryRequest.getMessage();
        }

        // 查询schema信息
        String dataSourceType = queryDatabaseType(queryRequest);
        String properties = "";
        if (CollectionUtils.isNotEmpty(queryRequest.getTableNames())) {
            TableQueryParam queryParam = chatConverter.chat2tableQuery(queryRequest);
            properties = buildTableColumn(queryParam, queryRequest.getTableNames());
        } else {
            properties = mappingDatabaseSchema(queryRequest);
        }
        String prompt = queryRequest.getMessage();
        String promptType = StringUtils.isBlank(queryRequest.getPromptType()) ? PromptType.NL_2_SQL.getCode()
            : queryRequest.getPromptType();
        PromptType pType = EasyEnumUtils.getEnum(PromptType.class, promptType);
        String ext = StringUtils.isNotBlank(queryRequest.getExt()) ? queryRequest.getExt() : "";
        String schemaProperty = StringUtils.isNotEmpty(properties) ? String.format(
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
        String cleanedInput = schemaProperty.replaceAll("[\r\t]", "");
        return cleanedInput;
    }

    public String mappingDatabaseSchema(ChatQueryRequest queryRequest) {
        String properties = "";
        String apiKey = getApiKey();
        if (StringUtils.isNotBlank(apiKey)) {
            boolean res = gatewayClientService.checkInWhite(new WhiteListRequest(apiKey, WhiteListTypeEnum.VECTOR.getCode())).getData();
            if (res) {
//                properties = queryDatabaseSchema(queryRequest) + querySchemaByEs(queryRequest);
                properties = queryDatabaseSchema(queryRequest);
            }
        }
        return properties;
    }


    /**
     * query chat2db apikey
     *
     * @return
     */
    public String getApiKey() {
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config config = configService.find(RestAIClient.AI_SQL_SOURCE).getData();
        String aiSqlSource = AiSqlSourceEnum.CHAT2DBAI.getCode();
        // only sync for chat2db ai
        if (Objects.isNull(config) || !aiSqlSource.equals(config.getContent())) {
            return null;
        }
        Config keyConfig = configService.find(Chat2dbAIClient.CHAT2DB_OPENAI_KEY).getData();
        if (Objects.isNull(keyConfig) || StringUtils.isBlank(keyConfig.getContent())) {
            return null;
        }
        return keyConfig.getContent();
    }

    /**
     * 构建schema参数
     *
     * @param tableQueryParam
     * @param tableNames
     * @return
     */
    public String buildTableColumn(TableQueryParam tableQueryParam,
        List<String> tableNames) {
        if (CollectionUtils.isEmpty(tableNames)) {
            return "";
        }
        List<String> schemaContent = Lists.newArrayList();
        try {
             schemaContent = tableNames.stream().map(tableName -> {
                tableQueryParam.setTableName(tableName);
                return queryTableDdl(tableName, tableQueryParam);
            }).collect(Collectors.toList());
        } catch (Exception exception) {
            log.error("query table error, do nothing");
        }

        return JSON.toJSONString(schemaContent);
    }

     /**
     * query table schema
     *
     * @param tableName
     * @param request
     * @return
     */
    public String queryTableDdl(String tableName, TableQueryParam request) {
        ShowCreateTableParam param = new ShowCreateTableParam();
        param.setTableName(tableName);
        param.setDataSourceId(request.getDataSourceId());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        DataResult<String> tableSchema = tableService.showCreateTable(param);
        return tableSchema.getData();
    }

    /**
     * query database schema
     *
     * @param queryRequest
     * @return
     * @throws IOException
     */
    public String queryDatabaseSchema(ChatQueryRequest queryRequest) {
        // request embedding
        FastChatEmbeddingResponse response = distributeAIEmbedding(queryRequest.getMessage());
        List<List<BigDecimal>> contentVector = new ArrayList<>();
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getData())) {
            return "";
        }
        contentVector.add(response.getData().get(0).getEmbedding());

        // search embedding
        TableSchemaRequest tableSchemaRequest = new TableSchemaRequest();
        tableSchemaRequest.setSchemaVector(contentVector);
        tableSchemaRequest.setDataSourceId(queryRequest.getDataSourceId());
        tableSchemaRequest.setDatabaseName(queryRequest.getDatabaseName());
        tableSchemaRequest.setDataSourceSchema(queryRequest.getSchemaName());
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config keyConfig = configService.find(Chat2dbAIClient.CHAT2DB_OPENAI_KEY).getData();
        if (Objects.isNull(keyConfig) || StringUtils.isBlank(keyConfig.getContent())) {
            return "";
        }
        tableSchemaRequest.setApiKey(keyConfig.getContent());
        try {
            DataResult<TableSchemaResponse> result = gatewayClientService.schemaVectorSearch(tableSchemaRequest);
            List<String> schemas = Lists.newArrayList();
            if (Objects.nonNull(result.getData()) && CollectionUtils.isNotEmpty(result.getData().getTableSchemas())) {
                for(TableSchema data: result.getData().getTableSchemas()){
                    schemas.add(data.getTableSchema());
                }
            }
            if (CollectionUtils.isEmpty(schemas)) {
                return "";
            }
            String res = JSON.toJSONString(schemas);
            log.info("search vector result:{}", res);
            return res;
        } catch (Exception exception) {
            log.error("query table error, do nothing");
            return "";
        }
    }

    /**
     * distribute embedding with different AI
     *
     * @return
     */
    public FastChatEmbeddingResponse distributeAIEmbedding(String input) {
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);
        Config config = configService.find(RestAIClient.AI_SQL_SOURCE).getData();
        String aiSqlSource = config.getContent();
        if (Objects.isNull(aiSqlSource)) {
            return null;
        }
        AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(aiSqlSource);
        switch (Objects.requireNonNull(aiSqlSourceEnum)) {
            case CHAT2DBAI:
                return embeddingWithChat2dbAi(input);
            case FASTCHATAI:
                return embeddingWithFastChatAi(input);
        }
        return null;
    }

    /**
     * embedding with fast chat openai
     *
     * @param input
     * @return
     * @throws IOException
     */
    public FastChatEmbeddingResponse embeddingWithFastChatAi(String input) {
        FastChatEmbeddingResponse response = FastChatAIClient.getInstance().embeddings(input);
        return response;
    }

    /**
     * embedding with open ai
     *
     * @param input
     * @return
     */
    public FastChatEmbeddingResponse embeddingWithChat2dbAi(String input) {
        FastChatEmbeddingResponse embeddings = Chat2dbAIClient.getInstance().embeddings(input);
        return embeddings;
    }

    /**
     * 构建prompt
     *
     * @param queryRequest
     * @return
     */
    public String buildAutoPrompt(ChatQueryRequest queryRequest) {
        if (PromptType.TEXT_GENERATION.getCode().equals(queryRequest.getPromptType())) {
            return queryRequest.getMessage();
        }
        // 查询schema信息
        String dataSourceType = queryDatabaseType(queryRequest);
        String properties = "";
        if (CollectionUtils.isNotEmpty(queryRequest.getTableNames())) {
            properties = queryRequest.getTableNames().stream().collect(Collectors.joining(","));
        } else {
            properties = queryDatabaseTables(queryRequest);
        }
        String prompt = queryRequest.getMessage();
        String promptType = StringUtils.isBlank(queryRequest.getPromptType()) ? PromptType.NL_2_SQL.getCode()
                : queryRequest.getPromptType();
        PromptType pType = EasyEnumUtils.getEnum(PromptType.class, promptType);
        if (pType.equals(PromptType.NL_2_SQL)) {
            pType = PromptType.FUNCTION_CALL;
        }

        String ext = StringUtils.isNotBlank(queryRequest.getExt()) ? queryRequest.getExt() : "";
        String schemaProperty = StringUtils.isNotEmpty(properties) ? String.format(
                "### 请根据以下table properties和SQL input%s. %s\n#\n### %s SQL tables:\n#\n# "
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
        String cleanedInput = schemaProperty.replaceAll("[\r\t]", "");
        return cleanedInput;
    }


    /**
     * query database type
     *
     * @param queryRequest
     * @return
     */
    public String queryDatabaseType(ChatQueryRequest queryRequest) {
        // 查询schema信息
        DataResult<DataSource> dataResult = dataSourceService.queryById(queryRequest.getDataSourceId());
        String dataSourceType = dataResult.getData().getType();
        if (StringUtils.isBlank(dataSourceType)) {
            dataSourceType = "MYSQL";
        }
        return dataSourceType;
    }

    /**
     * query database schema
     *
     * @param queryRequest
     * @return
     * @throws IOException
     */
    public String queryDatabaseTables(ChatQueryRequest queryRequest) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        try {
            List<Table> tables = metaSchema.tables(Chat2DBContext.getConnection(), queryRequest.getDatabaseName(), queryRequest.getSchemaName(), null);
            return tables.stream()
                    .map(table -> StringUtils.isBlank(table.getComment()) ? table.getName()
                            : table.getName() + "(" + table.getComment() + ")")
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            log.error("query table error:{}, do nothing", e.getMessage());
            return "";
        }
    }

    public static ToolsFunction getToolsFunction(){
        return ToolsFunction.builder()
                .name("get_table_columns")
                .description("获取指定表的字段名，类型")
                .parameters(Parameters.builder()
                        .type("object")
                        .properties(ImmutableMap.builder()
                                .put("table_name", ImmutableMap.builder()
                                        .put("type", "string")
                                        .put("description", "表名，例如```User```")
                                        .build())
                                .build())
                        .required(List.of("table_name"))
                        .build())
                .build();
    }


    /**
     * get fast chat message
     *
     * @param uid
     * @param prompt
     * @return
     */
    public List<FastChatMessage> getFastChatMessage(String uid, String prompt) {
        List<FastChatMessage> messages = (List<FastChatMessage>)LocalCache.CACHE.get(uid);
        if (CollectionUtils.isNotEmpty(messages)) {
            if (messages.size() >= contextLength) {
                messages = messages.subList(1, contextLength);
            }
        } else {
            messages = Lists.newArrayList();
        }
        FastChatMessage currentMessage = new FastChatMessage(FastChatRole.USER).setContent(prompt);
        messages.add(currentMessage);
        return messages;
    }
}
