package ai.chat2db.server.web.api.http;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.http.response.ApiKeyResponse;
import ai.chat2db.server.web.api.http.response.InviteQrCodeResponse;
import ai.chat2db.server.web.api.http.response.QrCodeResponse;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Var;

/**
 * Gateway 的http 服务
 *
 * @author Jiaju Zhuang
 */
@BaseRequest(
    baseURL = "{gatewayBaseUrl}"
)
public interface GatewayClientService {
    /**
     * 获取公众号的二维码
     *
     * @return
     */
    @Get("/api/client/loginQrCode")
    DataResult<QrCodeResponse> getLoginQrCode();

    /**
     * Refresh login
     *
     * @param token
     * @return
     */
    @Get("/api/client/loginStatus")
    DataResult<QrCodeResponse> getLoginStatus(@Query("token") String token);

    /**
     * 返回剩余次数
     *
     * @param key
     * @return
     */
    @Get("/api/client/remaininguses/{key}")
    DataResult<ApiKeyResponse> remaininguses(@Var("key") String key);


    /**
     * Obtain invitation QR code
     *
     * @param apiKey
     * @return
     */
    @Get("/api/client/inviteQrCode")
    DataResult<InviteQrCodeResponse> getInviteQrCode(@Query("apiKey") String apiKey);

}
