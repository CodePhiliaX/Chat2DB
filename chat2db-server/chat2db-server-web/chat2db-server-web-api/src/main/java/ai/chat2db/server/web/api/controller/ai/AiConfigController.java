package ai.chat2db.server.web.api.controller.ai;

import java.util.Objects;

import ai.chat2db.server.domain.api.enums.AiSqlSourceEnum;
import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.ai.rest.client.RestAIClient;
import ai.chat2db.server.web.api.http.GatewayClientService;
import ai.chat2db.server.web.api.http.response.ApiKeyResponse;
import ai.chat2db.server.web.api.http.response.InviteQrCodeResponse;
import ai.chat2db.server.web.api.http.response.QrCodeResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI configuration information interface
 *
 * @author Jiaju Zhuang
 */
@RestController
@ConnectionInfoAspect
@RequestMapping("/api/ai/config")
@Slf4j
public class AiConfigController {

    @Resource
    private GatewayClientService gatewayClientService;

    @Autowired
    private ConfigService configService;
    @Resource
    private Chat2dbProperties chat2dbProperties;

    /**
     * AI configuration information interface
     *
     * @return
     */
    @GetMapping("/getLoginQrCode")
    public DataResult<QrCodeResponse> getLoginQrCode() {
        LoginUser loginUser = ContextUtils.getLoginUser();
        if (RoleCodeEnum.USER.getCode().equals(loginUser.getRoleCode())) {
            return DataResult.of(
                QrCodeResponse.builder().tip(I18nUtils.getMessage("settings.permissionDeniedForAiConfig")).build());
        }
        return gatewayClientService.getLoginQrCode();
    }

    /**
     * Query login status
     *
     * @param token
     * @return
     */
    @GetMapping("/getLoginStatus")
    public DataResult<QrCodeResponse> getLoginStatus(@RequestParam(required = false) String token) {
        LoginUser loginUser = ContextUtils.getLoginUser();
        if (RoleCodeEnum.USER.getCode().equals(loginUser.getRoleCode())) {
            return DataResult.of(QrCodeResponse.builder().build());
        }
        DataResult<QrCodeResponse> dataResult = gatewayClientService.getLoginStatus(token);
        QrCodeResponse qrCodeResponse = dataResult.getData();
        // Representative successfully logged in
        if (StringUtils.isNotBlank(qrCodeResponse.getApiKey())) {
            SystemConfigParam sqlSourceParam = SystemConfigParam.builder().code(RestAIClient.AI_SQL_SOURCE)
                .content(AiSqlSourceEnum.CHAT2DBAI.getCode()).build();
            configService.createOrUpdate(sqlSourceParam);
            SystemConfigParam param = SystemConfigParam.builder()
                .code(Chat2dbAIClient.CHAT2DB_OPENAI_KEY).content(qrCodeResponse.getApiKey())
                .build();
            configService.createOrUpdate(param);
            SystemConfigParam hostParam = SystemConfigParam.builder()
                .code(Chat2dbAIClient.CHAT2DB_OPENAI_HOST)
                .content(chat2dbProperties.getGateway().getModelBaseUrl() + "/model")
                .build();
            configService.createOrUpdate(hostParam);
            Chat2dbAIClient.refresh();
        }
        return dataResult;
    }

    /**
     * Return remaining times
     *
     * @return
     */
    @GetMapping("/remaininguses")
    public DataResult<ApiKeyResponse> remaininguses() {
        String apiKey = getApiKey();
        if (StringUtils.isBlank(apiKey)) {
            return DataResult.of(ApiKeyResponse.builder()
                .build());
        }
        return gatewayClientService.remaininguses(apiKey);
    }

    /**
     * Obtain invitation QR code
     *
     * @return
     */
    @GetMapping("/getInviteQrCode")
    public DataResult<InviteQrCodeResponse> getInviteQrCode() {
        String apiKey = getApiKey();
        if (StringUtils.isBlank(apiKey)) {
            return DataResult.of(new InviteQrCodeResponse());
        }
        return gatewayClientService.getInviteQrCode(apiKey);
    }

    private String getApiKey() {
        DataResult<Config> apiKey = configService.find(Chat2dbAIClient.CHAT2DB_OPENAI_KEY);
        return Objects.nonNull(apiKey.getData()) ? apiKey.getData().getContent() : null;
    }
}
