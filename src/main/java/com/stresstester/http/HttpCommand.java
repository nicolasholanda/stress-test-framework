package com.stresstester.http;

import com.stresstester.config.HttpRequestConfig;
import com.stresstester.exception.HttpClientException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class HttpCommand {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0], null);

    private final OkHttpClient client;
    private final HttpRequestConfig config;

    public HttpCommand(OkHttpClient client, HttpRequestConfig config) {
        this.client = client;
        this.config = config;
    }

    public HttpResponse execute() {
        Request request = buildRequest();
        long startNanos = System.nanoTime();
        try (Response response = client.newCall(request).execute()) {
            long latencyMs = (System.nanoTime() - startNanos) / 1_000_000;
            String responseBody = response.body() != null ? response.body().string() : "";
            return new HttpResponse(response.code(), responseBody, latencyMs);
        } catch (IOException e) {
            throw new HttpClientException("Request to " + config.getUrl() + " failed: " + e.getMessage(), e);
        }
    }

    private Request buildRequest() {
        RequestBody requestBody = config.getBody() != null
                ? RequestBody.create(config.getBody(), JSON)
                : null;

        Request.Builder builder = new Request.Builder().url(config.getUrl());
        config.getHeaders().forEach(builder::addHeader);

        switch (config.getMethod()) {
            case GET     -> builder.get();
            case HEAD    -> builder.head();
            case POST    -> builder.post(requestBody != null ? requestBody : EMPTY_BODY);
            case PUT     -> builder.put(requestBody != null ? requestBody : EMPTY_BODY);
            case PATCH   -> builder.patch(requestBody != null ? requestBody : EMPTY_BODY);
            case DELETE  -> { if (requestBody != null) builder.delete(requestBody); else builder.delete(); }
            case OPTIONS -> builder.method("OPTIONS", requestBody);
        }

        return builder.build();
    }
}
