package ai.chat2db.server.web.api.controller.ai.baichuan.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;

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

        // Get the current timestamp (UTC standard timestamp)
        long timestamp = System.currentTimeMillis() / 1000;

        // Get the original HTTP-Body
        RequestBody originalRequestBody = originalRequest.body();
        Buffer buffer = new Buffer();
        if (originalRequestBody != null) {
            originalRequestBody.writeTo(buffer);
        }
        String httpBody = buffer.readUtf8();

        // Calculate X-BC-Signature
        String signature = calculateSignature(secretKey, httpBody, timestamp);

        //Create a new request and add custom request headers
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-BC-Sign-Algo", "MD5")
                .addHeader("X-BC-Timestamp", String.valueOf(timestamp))
                .addHeader("X-BC-Signature", signature)
                .method(originalRequest.method(), RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), httpBody))
                .build();

        return chain.proceed(newRequest);
    }

    private String calculateSignature(String secretKey, String httpBody, long timestamp) {
        String toHash = secretKey + httpBody + timestamp;
        return md5(toHash);
    }

    private String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("baichuan secret key md5 error", e);
            return "";
        }
    }

    private String calculateSignature(String secretKey, RequestBody body, long timestamp) {
        try {
            String requestBody = bodyToString(body);
            String rawSignature = secretKey + requestBody + timestamp;

            // Use MD5 to calculate signature
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(rawSignature.getBytes(StandardCharsets.UTF_8));

            // Convert MD5 byte array to Base64 encoded string
            return Base64.getEncoder().encodeToString(mdBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("baichuan secret key md5 error", e);
            return "";
        }
    }

    private String bodyToString(RequestBody body) throws IOException {
        //Convert RequestBody to string
        return body == null ? "" : body.toString();
    }
}

