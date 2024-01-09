package ai.chat2db.server.web.api.http;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.web.api.http.request.*;
import ai.chat2db.server.web.api.http.response.*;
import com.dtflys.forest.Forest;
import com.dtflys.forest.utils.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * Gateway 的http 服务
 *
 * @author Jiaju Zhuang
 */
//@BaseRequest(
//    baseURL = "{gatewayBaseUrl}"
//)
@Service
public class GatewayClientService {

    @Resource
    private Chat2dbProperties chat2dbProperties;

    /**
     * 获取公众号的二维码
     *
     * @return
     */
    public DataResult<QrCodeResponse> getLoginQrCode() {
        DataResult<QrCodeResponse> result = Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/loginQrCode")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * Refresh login
     *
     * @param token
     * @return
     */
    public DataResult<QrCodeResponse> getLoginStatus(String token) {
        DataResult<QrCodeResponse> result = Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/loginStatus")
                .connectTimeout(Duration.ofMillis(5000))
                .addQuery("token", token)
                .readTimeout(Duration.ofMillis(10000))
                .execute(new TypeReference<>() {
                });
        return result;

    }

    /**
     * 返回剩余次数
     *
     * @param key
     * @return
     */
    public DataResult<ApiKeyResponse> remaininguses(String key) {
        DataResult<ApiKeyResponse> result = Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/remaininguses/" + key)
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .execute(new TypeReference<>() {
                });
        return result;

    }


    /**
     * Obtain invitation QR code
     *
     * @param apiKey
     * @return
     */
    public DataResult<InviteQrCodeResponse> getInviteQrCode(String apiKey) {
        DataResult<InviteQrCodeResponse> result = Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/inviteQrCode")
                .connectTimeout(Duration.ofMillis(5000))
                .addQuery("apiKey", apiKey)
                .readTimeout(Duration.ofMillis(10000))
                .execute(new TypeReference<>() {
                });
        return result;


    }

    /**
     * save knowledge vector
     *
     * @param request
     * @return
     */
    public ActionResult knowledgeVectorSave(KnowledgeRequest request) {

        ActionResult result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/milvus/knowledge/save")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;

    }

    /**
     * save table schema vector
     *
     * @param request
     * @return
     */
    public ActionResult schemaVectorSave(TableSchemaRequest request) {
        ActionResult result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/milvus/schema/save")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * save table schema vector
     *
     * @param request
     * @return
     */
    public ActionResult schemaEsSave(EsTableSchemaRequest request) {
        ActionResult result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/es/schema/save")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * save knowledge vector
     *
     * @param searchVectors
     * @return
     */
    public DataResult<KnowledgeResponse> knowledgeVectorSearch(KnowledgeRequest searchVectors) {
        DataResult<KnowledgeResponse> result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/milvus/knowledge/search")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(searchVectors)
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * save table schema vector
     *
     * @param request
     * @return
     */
    public DataResult<TableSchemaResponse> schemaVectorSearch(TableSchemaRequest request) {
        DataResult<TableSchemaResponse> result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/milvus/schema/search")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * save table schema vector
     *
     * @param request
     * @return
     */
    public DataResult<EsTableSchemaResponse> schemaEsSearch(EsTableSchemaRequest request) {
        DataResult<EsTableSchemaResponse> result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/es/schema/search")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;
    }

    /**
     * check in white list
     *
     * @param whiteListRequest
     * @return
     */
    public DataResult<Boolean> checkInWhite(WhiteListRequest whiteListRequest) {
        // 去掉白名单
        return DataResult.of(false);
//        DataResult<Boolean> result = Forest.get(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/whitelist/check")
//                .connectTimeout(Duration.ofMillis(5000))
//                .readTimeout(Duration.ofMillis(10000))
//                .addQuery(whiteListRequest)
//                .execute(new TypeReference<>() {
//                });
//        return result;
    }

    public ActionResult addOperationLog(SqlExecuteHistoryCreateRequest request) {
        ActionResult result = Forest.post(chat2dbProperties.getGateway().getBaseUrl() + "/api/client/sql/execute/history")
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .contentType("application/json")
                .addBody(request)
                .execute(new TypeReference<>() {
                });
        return result;
    }
}
