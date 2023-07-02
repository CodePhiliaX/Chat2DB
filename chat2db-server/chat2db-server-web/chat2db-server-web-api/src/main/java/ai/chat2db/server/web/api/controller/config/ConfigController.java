/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.web.api.controller.config;

import java.util.Objects;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.model.ChatGptConfig;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ai.azure.client.AzureOpenAIClient;
import ai.chat2db.server.web.api.controller.config.request.AISystemConfigRequest;
import ai.chat2db.server.web.api.controller.config.request.SystemConfigRequest;
import ai.chat2db.server.web.api.util.OpenAIClient;
import ai.chat2db.server.web.api.controller.ai.rest.client.RestAIClient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jipengfei
 * @version : ConfigController.java
 */
@ConnectionInfoAspect
@RequestMapping("/api/config")
@RestController
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("/system_config")
    public ActionResult systemConfig(@RequestBody SystemConfigRequest request) {
        SystemConfigParam param = SystemConfigParam.builder().code(request.getCode()).content(request.getContent())
            .build();
        configService.createOrUpdate(param);
        if (OpenAIClient.OPENAI_KEY.equals(request.getCode())) {
            OpenAIClient.refresh();
        }
        return ActionResult.isSuccess();
    }

    /**
     * 保存ChatGPT相关配置
     *
     * @param request
     * @return
     */
    @PostMapping("/system_config/chatgpt")
    public ActionResult addChatGptSystemConfig(@RequestBody AISystemConfigRequest request) {
        String sqlSource = StringUtils.isNotBlank(request.getAiSqlSource()) ? request.getAiSqlSource()
            : AiSqlSourceEnum.OPENAI.getCode();
        AiSqlSourceEnum aiSqlSourceEnum = AiSqlSourceEnum.getByName(sqlSource);
        if (Objects.isNull(aiSqlSourceEnum)) {
            aiSqlSourceEnum = AiSqlSourceEnum.OPENAI;
            sqlSource = AiSqlSourceEnum.OPENAI.getCode();
        }
        SystemConfigParam param = SystemConfigParam.builder().code(RestAIClient.AI_SQL_SOURCE).content(sqlSource)
            .build();
        configService.createOrUpdate(param);

        switch (Objects.requireNonNull(aiSqlSourceEnum)) {
            case OPENAI :
            case CHAT2DBAI:
                saveOpenAIConfig(request);
            case RESTAI :
                saveRestAIConfig(request);
            case AZUREAI :
                saveAzureAIConfig(request);
        }
        return ActionResult.isSuccess();
    }

    /**
     * 保存OPENAI相关配置
     *
     * @param request
     */
    private void saveOpenAIConfig(AISystemConfigRequest request) {
        SystemConfigParam param = SystemConfigParam.builder().code(OpenAIClient.OPENAI_KEY).content(
                request.getApiKey())
            .build();
        configService.createOrUpdate(param);
        SystemConfigParam hostParam = SystemConfigParam.builder().code(OpenAIClient.OPENAI_HOST).content(
                request.getApiHost())
            .build();
        configService.createOrUpdate(hostParam);
        SystemConfigParam httpProxyHostParam = SystemConfigParam.builder().code(OpenAIClient.PROXY_HOST).content(
            request.getHttpProxyHost()).build();
        configService.createOrUpdate(httpProxyHostParam);
        SystemConfigParam httpProxyPortParam = SystemConfigParam.builder().code(OpenAIClient.PROXY_PORT).content(
            request.getHttpProxyPort()).build();
        configService.createOrUpdate(httpProxyPortParam);
        OpenAIClient.refresh();
    }

    /**
     * 保存RESTAI接口相关配置
     *
     * @param request
     */
    private void saveRestAIConfig(AISystemConfigRequest request) {
        SystemConfigParam restParam = SystemConfigParam.builder().code(RestAIClient.REST_AI_URL).content(
                request.getRestAiUrl())
            .build();
        configService.createOrUpdate(restParam);
        SystemConfigParam methodParam = SystemConfigParam.builder().code(RestAIClient.REST_AI_STREAM_OUT).content(
            request.getRestAiStream().toString()).build();
        configService.createOrUpdate(methodParam);
        RestAIClient.refresh();
    }

    /**
     * 保存azure配置
     *
     * @param request
     */
    private void saveAzureAIConfig(AISystemConfigRequest request) {
        SystemConfigParam apikeyParam = SystemConfigParam.builder().code(AzureOpenAIClient.AZURE_CHATGPT_API_KEY).content(
            request.getAzureApiKey()).build();
        configService.createOrUpdate(apikeyParam);
        SystemConfigParam endpointParam = SystemConfigParam.builder().code(AzureOpenAIClient.AZURE_CHATGPT_ENDPOINT).content(
            request.getAzureEndpoint()).build();
        configService.createOrUpdate(endpointParam);
        SystemConfigParam modelParam = SystemConfigParam.builder().code(AzureOpenAIClient.AZURE_CHATGPT_DEPLOYMENT_ID).content(
            request.getAzureDeploymentId()).build();
        configService.createOrUpdate(modelParam);
        AzureOpenAIClient.refresh();
    }

    @GetMapping("/system_config/{code}")
    public DataResult<Config> getSystemConfig(@PathVariable("code") String code) {
        DataResult<Config> result = configService.find(code);
        return DataResult.of(result.getData());
    }

    /**
     * 查询ChatGPT相关配置
     *
     * @return
     */
    @GetMapping("/system_config/chatgpt")
    public DataResult<ChatGptConfig> getChatGptSystemConfig() {
        DataResult<Config> apiKey = configService.find(OpenAIClient.OPENAI_KEY);
        DataResult<Config> apiHost = configService.find(OpenAIClient.OPENAI_HOST);
        DataResult<Config> httpProxyHost = configService.find(OpenAIClient.PROXY_HOST);
        DataResult<Config> httpProxyPort = configService.find(OpenAIClient.PROXY_PORT);
        DataResult<Config> aiSqlSource = configService.find(RestAIClient.AI_SQL_SOURCE);
        DataResult<Config> restAiUrl = configService.find(RestAIClient.REST_AI_URL);
        DataResult<Config> restAiHttpMethod = configService.find(RestAIClient.REST_AI_STREAM_OUT);
        DataResult<Config> azureApiKey = configService.find(AzureOpenAIClient.AZURE_CHATGPT_API_KEY);
        DataResult<Config> azureEndpoint = configService.find(AzureOpenAIClient.AZURE_CHATGPT_ENDPOINT);
        DataResult<Config> azureDeployId = configService.find(AzureOpenAIClient.AZURE_CHATGPT_DEPLOYMENT_ID);
        ChatGptConfig config = new ChatGptConfig();
        config.setApiHost(Objects.nonNull(apiHost.getData()) ? apiHost.getData().getContent() : null);
        config.setAiSqlSource(Objects.nonNull(aiSqlSource.getData()) ? aiSqlSource.getData().getContent() : null);
        config.setRestAiUrl(Objects.nonNull(restAiUrl.getData()) ? restAiUrl.getData().getContent() : null);
        config.setRestAiStream(Objects.nonNull(restAiHttpMethod.getData()) ? Boolean.valueOf(
            restAiHttpMethod.getData().getContent()) : Boolean.TRUE);
        config.setApiKey(Objects.nonNull(apiKey.getData()) ? apiKey.getData().getContent() : null);
        config.setHttpProxyHost(Objects.nonNull(httpProxyHost.getData()) ? httpProxyHost.getData().getContent() : null);
        config.setHttpProxyPort(Objects.nonNull(httpProxyPort.getData()) ? httpProxyPort.getData().getContent() : null);
        config.setAzureApiKey(Objects.nonNull(azureApiKey.getData()) ? azureApiKey.getData().getContent() : null);
        config.setAzureEndpoint(Objects.nonNull(azureEndpoint.getData()) ? azureEndpoint.getData().getContent() : null);
        config.setAzureDeploymentId(Objects.nonNull(azureDeployId.getData()) ? azureDeployId.getData().getContent() : null);
        return DataResult.of(config);
    }
}
