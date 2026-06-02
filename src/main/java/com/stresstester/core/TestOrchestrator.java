package com.stresstester.core;

import com.stresstester.config.TestConfig;
import com.stresstester.http.HttpClientFactory;
import com.stresstester.load.LoadStrategy;
import com.stresstester.load.LoadStrategyFactory;
import com.stresstester.metrics.MetricsCollector;
import com.stresstester.model.TestResult;
import okhttp3.OkHttpClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestOrchestrator extends StressTestEngine {

    private MetricsCollector metricsCollector;
    private OkHttpClient httpClient;
    private ExecutorService executor;
    private AtomicBoolean running;

    @Override
    protected void setup(TestConfig config) {
        metricsCollector = new MetricsCollector();
        httpClient = HttpClientFactory.create(config.getRequestTimeout());
        executor = Executors.newCachedThreadPool();
        running = new AtomicBoolean(true);
    }

    @Override
    protected void executeLoad(TestConfig config) {
        LoadStrategy strategy = LoadStrategyFactory.create(config.getLoadProfile());
        Instant startTime = Instant.now();
        int currentUsers = 0;

        while (true) {
            long elapsedMs = Duration.between(startTime, Instant.now()).toMillis();
            long completed = metricsCollector.getTotalCompletedRequests();

            if (strategy.isDone(elapsedMs, completed, config)) {
                break;
            }

            int targetUsers = strategy.getActiveUsers(elapsedMs, config);
            while (currentUsers < targetUsers) {
                executor.submit(new VirtualUser(httpClient, config.getRequestConfig(), metricsCollector, running));
                currentUsers++;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        running.set(false);
        metricsCollector.finish();
    }

    @Override
    protected TestResult collectResults() {
        return metricsCollector.buildResult();
    }

    @Override
    protected void teardown() {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
