package ai.chat2db.server.web.api.controller.ai.wenxin.interceptor;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AccessTokenInterceptor implements Interceptor {
    private final String accessToken;

    public AccessTokenInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();

        // Use HttpUrl.Builder to add query parameter access_token
        HttpUrl urlWithAccessToken = originalHttpUrl.newBuilder()
                .addQueryParameter("access_token", accessToken)
                .build();

        // Create a new request and apply the new URL to it
        Request newRequest = originalRequest.newBuilder()
                .url(urlWithAccessToken)
                .build();

        return chain.proceed(newRequest);
    }
}

