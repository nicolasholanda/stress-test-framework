package com.stresstester.http;

import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HttpClientFactory {

    private HttpClientFactory() {}

    public static OkHttpClient create(Duration timeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }
}
