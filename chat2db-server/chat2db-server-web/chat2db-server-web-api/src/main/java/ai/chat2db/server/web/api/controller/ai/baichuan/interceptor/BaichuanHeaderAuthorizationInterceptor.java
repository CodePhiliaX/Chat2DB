package ai.chat2db.server.web.api.controller.ai.baichuan.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * header apikey
 *
 * @author grt
 * @since 2023-03-23
 */
@Slf4j
@Getter
public class BaichuanHeaderAuthorizationInterceptor implements Interceptor {

    private String apiKey;

    private String secretKey;

    public BaichuanHeaderAuthorizationInterceptor(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 获取当前的时间戳（UTC标准时间戳）
        long timestamp = System.currentTimeMillis() / 1000;

        // 构造 HTTP-Body，这里需要根据实际情况构造你的请求体
        // 这里示例构造一个空的请求体
        RequestBody requestBody = RequestBody.create("", MediaType.parse("text/plain"));

        // 计算 X-BC-Signature
        String signature = calculateSignature(secretKey, requestBody, timestamp);

        // 创建新的请求，并添加自定义请求头
        Request newRequest = originalRequest.newBuilder()
                .addHeader(Header.AUTHORIZATION.getValue(), "Bearer " + apiKey)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .addHeader("X-BC-Sign-Algo", "MD5")
                .addHeader("X-BC-Timestamp", String.valueOf(timestamp))
                .addHeader("X-BC-Signature", signature)
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(newRequest);
    }

    private String calculateSignature(String secretKey, RequestBody body, long timestamp) {
        try {
            String requestBody = bodyToString(body);
            String rawSignature = secretKey + requestBody + timestamp;

            // 使用 MD5 计算签名
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(rawSignature.getBytes(StandardCharsets.UTF_8));

            // 将 MD5 字节数组转换为 Base64 编码的字符串
            return Base64.getEncoder().encodeToString(mdBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("baichuan secret key md5 error", e);
            return "";
        }
    }

    private String bodyToString(RequestBody body) throws IOException {
        // 将 RequestBody 转换为字符串
        return body == null ? "" : body.toString();
    }
}

