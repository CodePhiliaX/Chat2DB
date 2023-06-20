package com.alibaba.dbhub.server.test.temp;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.util.Arrays;

@Slf4j
public class TempTest {


    @Test
    public void test() throws Exception {
System.setProperty("jdk.tls.disabledAlgorithms","SLv3");
        log.info("pp:{}", JSON.toJSONString(SSLContext.getDefault().getSupportedSSLParameters().getProtocols()));

        OkHttpClient client = new OkHttpClient.Builder()
                //添加TLSv1、TLSv1.1、TLSv1.2、TLSv1.3支持
                .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS))
                .build();
        Request request = new Request.Builder()
                .url("https://test-oss-grow2.alibaba.com/latest-mac.yml")
                .build();

        String re = client.newCall(request).execute().body().string();

        log.info("re:{}", re);
//
//        String str = Forest.get("https://test-oss-grow2.alibaba.com/latest-mac.yml").executeAsString();
//        log.info("re:{}", str);
    }
}
