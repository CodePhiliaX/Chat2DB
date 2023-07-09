package ai.chat2db.server.web.api.controller.ai;

import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.http.GatewayClientServiceV2;
import ai.chat2db.server.web.api.http.response.ApiKeyResponse;
import ai.chat2db.server.web.api.http.response.QrCodeResponse;
import ai.chat2db.server.web.api.util.OpenAIClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    //@Autowired
    //private GatewayClientService gatewayClientService;

    @Autowired
    private GatewayClientServiceV2 gatewayClientService;
    @Autowired
    private ConfigService configService;
    @Resource
    private Chat2dbProperties chat2dbProperties;

    /**
     * AI configuration information interface
     *
     * @param token
     * @return
     */
    @GetMapping("/getQrCode")
    public DataResult<QrCodeResponse> getQrCode(@RequestParam(required = false) String token) {
        DataResult<QrCodeResponse> dataResult = gatewayClientService.getQrCode(token);
        QrCodeResponse qrCodeResponse = dataResult.getData();
        // Representative successfully logged in
        if (StringUtils.isNotBlank(qrCodeResponse.getApiKey())) {
            SystemConfigParam param = SystemConfigParam.builder()
                .code(OpenAIClient.OPENAI_KEY).content(qrCodeResponse.getApiKey())
                .build();
            configService.createOrUpdate(param);
            SystemConfigParam hostParam = SystemConfigParam.builder()
                .code(OpenAIClient.OPENAI_HOST)
                .content(chat2dbProperties.getGateway().getBaseUrl() + "/model")
                .build();
            configService.createOrUpdate(hostParam);
        }
        return dataResult;
    }

    /**
     * Return remaining times
     *
     * @param key
     * @return
     */
    @GetMapping("/remaininguses/{key}")
    public DataResult<ApiKeyResponse> remaininguses(@PathVariable String key) {
        return gatewayClientService.remaininguses(key);
    }

}
