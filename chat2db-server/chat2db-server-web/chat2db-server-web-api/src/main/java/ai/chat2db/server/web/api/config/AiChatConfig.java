package ai.chat2db.server.web.api.config;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.concurrent.Immutable;
import java.util.*;

@Service
public class AiChatConfig {
    private static final String MODEL_SERVICE_CONFIG_CODE = "ai.model.services";
    private static final String MODEL_DEFAULT_CONFIG_CODE = "ai.model.default";

    @Autowired
    private ConfigService configService;

    public ChatClient createChatClient(PromptType promptType) {
        ModelRuntimeConfig runtimeConfig = loadModelRuntimeConfig();
        boolean useFastModel = promptType != null && promptType.isSimpleTask()
                && runtimeConfig.fastModel != null && runtimeConfig.fastService != null;
        if (useFastModel) {
            return createChatClientByModel(runtimeConfig.fastService, runtimeConfig.fastModel, true);
        }
        return createChatClientByModel(runtimeConfig.defaultService, runtimeConfig.defaultModel, false);
    }

    public ChatClient createFastChatClient() {
        ModelRuntimeConfig runtimeConfig = loadModelRuntimeConfig();
        if (runtimeConfig.fastModel != null && runtimeConfig.fastService != null) {
            return createChatClientByModel(runtimeConfig.fastService, runtimeConfig.fastModel, true);
        }
        return createChatClientByModel(runtimeConfig.defaultService, runtimeConfig.defaultModel, false);
    }

    public ChatResponse testModelService(String provider, String apiHost, String apiKey, String model) {
        ModelServiceConfig service = new ModelServiceConfig();
        service.setProvider(provider);
        service.setApiHost(apiHost);
        service.setApiKey(apiKey);
        ModelItem modelItem = new ModelItem();
        modelItem.setModel(model);
        return createChatClientByModel(service, modelItem, true)
                .prompt("ping")
                .call()
                .chatResponse();
    }

    private ChatClient createChatClientByModel(ModelServiceConfig service, ModelItem modelItem, boolean fastMode) {
        AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(service.getProvider());
        if (aiSqlSourceEnum == null) {
            aiSqlSourceEnum = AiSqlSourceEnum.OPENAI;
        }

        if (aiSqlSourceEnum == AiSqlSourceEnum.ANTHROPIC) {
            AnthropicApi anthropicApi = AnthropicApi.builder().apiKey(service.getApiKey()).build();
            AnthropicChatOptions options = AnthropicChatOptions.builder()
                    .model(modelItem.getModel())
                    .temperature(fastMode ? 0.5 : 0.7)
                    .maxTokens(fastMode ? 1024 : 4096)
                    .build();
            AnthropicChatModel chatModel = AnthropicChatModel.builder()
                    .anthropicApi(anthropicApi)
                    .defaultOptions(options)
                    .build();
            return ChatClient.builder(chatModel).build();
        }

        String apiHost = service.getApiHost();
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(apiHost).apiKey(service.getApiKey()).build();
        Map<String, Object> extraBody = new HashMap<>();
        if (StringUtils.contains(modelItem.getModel(), "qwen")) {
            extraBody.put("enable_thinking", !fastMode);
        } else if (StringUtils.contains(modelItem.getModel(), "hy")) {
            extraBody.put("reasoning", ImmutableMap.of("enable", !fastMode));
        }
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(modelItem.getModel())
                .temperature(fastMode ? 0.5 : 0.7)
                .maxTokens(fastMode ? 1024 : 4096)
                .extraBody(extraBody)
                .build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
        return ChatClient.builder(chatModel).build();
    }

    private ModelRuntimeConfig loadModelRuntimeConfig() {
        String serviceJson = getConfigValue(MODEL_SERVICE_CONFIG_CODE);
        String defaultJson = getConfigValue(MODEL_DEFAULT_CONFIG_CODE);
        if (serviceJson.isEmpty() || defaultJson.isEmpty()) {
            throw new IllegalStateException("Model service or default model config is empty.");
        }

        List<ModelServiceConfig> serviceList = JSON.parseObject(serviceJson, new TypeReference<>() {
        });
        DefaultModelConfig defaultModelConfig = JSON.parseObject(defaultJson, DefaultModelConfig.class);
        if (serviceList == null || serviceList.isEmpty() || defaultModelConfig == null
                || defaultModelConfig.getDefaultModelId() == null || defaultModelConfig.getDefaultModelId().isEmpty()) {
            throw new IllegalStateException("Default model config is invalid.");
        }

        ModelLocateResult defaultResult = locateModel(serviceList, defaultModelConfig.getDefaultModelId());
        if (defaultResult == null) {
            throw new IllegalStateException("Default model not found in model services.");
        }

        ModelLocateResult fastResult = null;
        if (defaultModelConfig.getFastModelId() != null && !defaultModelConfig.getFastModelId().isEmpty()) {
            fastResult = locateModel(serviceList, defaultModelConfig.getFastModelId());
        }

        ModelRuntimeConfig runtimeConfig = new ModelRuntimeConfig();
        runtimeConfig.defaultService = defaultResult.service;
        runtimeConfig.defaultModel = defaultResult.model;
        if (fastResult != null) {
            runtimeConfig.fastService = fastResult.service;
            runtimeConfig.fastModel = fastResult.model;
        }
        return runtimeConfig;
    }

    private ModelLocateResult locateModel(List<ModelServiceConfig> serviceList, String modelId) {
        for (ModelServiceConfig service : serviceList) {
            if (service.getModelList() == null) {
                continue;
            }
            for (ModelItem model : service.getModelList()) {
                if (Objects.equals(model.getId(), modelId)) {
                    ModelLocateResult result = new ModelLocateResult();
                    result.service = service;
                    result.model = model;
                    return result;
                }
            }
        }
        return null;
    }

    private String getConfigValue(String code) {
        Config result = configService.find(code);
        if (result != null && result.getContent() != null
                && !result.getContent().isEmpty()) {
            return result.getContent();
        }
        return "";
    }

    @Data
    private static class DefaultModelConfig {
        private String defaultModelId;
        private String fastModelId;
    }

    @Data
    private static class ModelItem {
        private String id;
        private String model;
    }

    @Data
    private static class ModelServiceConfig {
        private String provider;
        private String apiKey;
        private String apiHost;
        private List<ModelItem> modelList = new ArrayList<>();
    }

    private static class ModelLocateResult {
        private ModelServiceConfig service;
        private ModelItem model;
    }

    private static class ModelRuntimeConfig {
        private ModelServiceConfig defaultService;
        private ModelItem defaultModel;
        private ModelServiceConfig fastService;
        private ModelItem fastModel;
    }
}
