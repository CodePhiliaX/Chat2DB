package ai.chat2db.server.web.api.controller.ai.fastchat.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * header apikey
 *
 * @author grt
 * @since 2023-03-23
 */
@Getter
public class FastChatHeaderAuthorizationInterceptor implements Interceptor {

    private String apiKey;

    public FastChatHeaderAuthorizationInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("apiKey", apiKey) // replace to your corresponding field and value
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
