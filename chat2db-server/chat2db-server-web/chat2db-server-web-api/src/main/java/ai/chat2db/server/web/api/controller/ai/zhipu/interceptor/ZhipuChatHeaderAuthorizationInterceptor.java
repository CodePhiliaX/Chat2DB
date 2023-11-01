package ai.chat2db.server.web.api.controller.ai.zhipu.interceptor;

import ai.chat2db.server.web.api.controller.ai.zhipu.util.ZhipuUtils;
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
public class ZhipuChatHeaderAuthorizationInterceptor implements Interceptor {

    private String key;

    private String secret;

    public ZhipuChatHeaderAuthorizationInterceptor(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = ZhipuUtils.getToken(key, secret);
        Request request = original.newBuilder()
                // replace to your corresponding field and value
                .header(Header.AUTHORIZATION.getValue(), token)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
