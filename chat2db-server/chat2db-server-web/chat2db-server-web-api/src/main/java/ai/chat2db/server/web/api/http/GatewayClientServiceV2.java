package ai.chat2db.server.web.api.http;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.web.api.http.response.ApiKeyResponse;
import ai.chat2db.server.web.api.http.response.QrCodeResponse;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.utils.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Gateway 的http 服务
 *
 * @author Jiaju Zhuang
 */
@Component
public class GatewayClientServiceV2 {
    @Resource
    private Chat2dbProperties chat2dbProperties;

    /**
     * 获取公众号的二维码
     *
     * @return
     */
    @Get("/api/client/getLoginQrCode")
    public DataResult<QrCodeResponse> getLoginQrCode() {
        return Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/getLoginQrCode")
            .execute(new TypeReference<>() {});
    }


    /**
     * Query login status
     *
     * @param token
     * @return
     */
    @Get("/api/client/getLoginStatus")
    public DataResult<QrCodeResponse> getLoginStatus(@Query("token") String token) {
        return Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/getLoginStatus")
            .addQuery("token", token)
            .execute(new TypeReference<>() {});
    }


    /**
     * 返回剩余次数
     *
     * @param key
     * @return
     */
    @Get("/api/client/remaininguses/{key}")
    public DataResult<ApiKeyResponse> remaininguses(@Var("key") String key) {
        return Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/remaininguses/" + key)
            .execute(new TypeReference<>() {});
    }

}
