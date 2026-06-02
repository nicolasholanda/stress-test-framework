package com.stresstester.core;

import com.stresstester.config.HttpRequestConfig;
import com.stresstester.exception.HttpClientException;
import com.stresstester.http.HttpCommand;
import com.stresstester.http.HttpResponse;
import com.stresstester.metrics.MetricsEvent;
import com.stresstester.metrics.MetricsListener;
import okhttp3.OkHttpClient;

import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualUser implements Runnable {

    private final OkHttpClient client;
    private final HttpRequestConfig requestConfig;
    private final MetricsListener metricsListener;
    private final AtomicBoolean running;

    public VirtualUser(OkHttpClient client, HttpRequestConfig requestConfig,
                       MetricsListener metricsListener, AtomicBoolean running) {
        this.client = client;
        this.requestConfig = requestConfig;
        this.metricsListener = metricsListener;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            HttpCommand command = new HttpCommand(client, requestConfig);
            try {
                HttpResponse response = command.execute();
                metricsListener.onEvent(MetricsEvent.success(response.getLatencyMs(), response.getStatusCode()));
            } catch (HttpClientException e) {
                metricsListener.onEvent(MetricsEvent.failure(e.getMessage()));
            }
        }
    }
}
