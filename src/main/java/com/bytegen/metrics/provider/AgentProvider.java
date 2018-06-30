package com.bytegen.metrics.provider;

import com.bytegen.metrics.constant.AgentConfig;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class AgentProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentProvider.class);
    private static final AgentConfig agentConfig = AgentConfig.getInstance();
    private static AgentProvider instance;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final int HTTP_CONNECTION_TIMEOUT = 800; // unit ms
    private final OkHttpClient client;
    private final String url;

    private AgentProvider() {
        client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new RetryInterceptor(3))
                .build();

        url = agentConfig.getAgentHostPort() + agentConfig.getAgentPathUri();
    }

    public static AgentProvider getInstance() {
        if (null == instance) {
            synchronized (AgentProvider.class) {
                if (null == instance) {
                    instance = new AgentProvider();
                }
            }
        }
        return instance;
    }

    public String send(String content) {
        RequestBody body = RequestBody.create(JSON_TYPE, content);
        Request request = new Request.Builder()
                .addHeader("Content-Type", JSON_TYPE.toString())
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Throwable e) {
            LOGGER.error("Metrics: unexpected exception. ", e);
        }
        return null;
    }

    static class RetryInterceptor implements Interceptor {

        private int maxRetry;
        private int retryNum = 0;

        public RetryInterceptor(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                LOGGER.warn("Metrics: falcon agent retried: [{}]/[{}]. ", retryNum, maxRetry);
                response = chain.proceed(request);
            }
            return response;
        }
    }
}