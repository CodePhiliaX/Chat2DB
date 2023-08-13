package ai.chat2db.server.web.api.controller.ai.claude.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 描述：请求增加header apikey
 *
 * @author grt
 * @since 2023-03-23
 */
@Getter
public class ClaudeHeaderAuthorizationInterceptor implements Interceptor {

    private String sessionKey;

    private String orgId;

    public ClaudeHeaderAuthorizationInterceptor(String sessionKey, String orgId) {
        this.orgId = orgId;
        this.sessionKey = sessionKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Cookie", "sessionKey=" + sessionKey)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
