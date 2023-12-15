package ai.chat2db.server.web.api.controller.ai.zhipu.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ZhipuUtils {

    private static final String tokenV3KeyPrefix = "zhipu_oapi_token_v3";


    public static String getToken(String key, String secret) {
        String newToken = createJwt(key, secret);
        return newToken;
    }

    private static String createJwt(String key, String secret) {
        Algorithm alg;
        try {
            alg = Algorithm.HMAC256(secret.getBytes("utf-8"));
        } catch (Exception e) {
            log.info("create jwt error", e);
            return null;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", key);
        payload.put("exp", System.currentTimeMillis() + 30 * 60 * 1000);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        String token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(alg);
        return token;
    }
}
