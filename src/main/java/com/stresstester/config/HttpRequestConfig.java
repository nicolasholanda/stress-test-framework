package com.stresstester.config;

import com.stresstester.model.HttpMethod;
import java.util.Map;

public class HttpRequestConfig {

    private final String url;
    private final HttpMethod method;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequestConfig(String url, HttpMethod method, Map<String, String> headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public String getUrl() { return url; }
    public HttpMethod getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
}
