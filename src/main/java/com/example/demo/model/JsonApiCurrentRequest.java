package com.example.demo.model;

import java.time.Instant;

public class JsonApiCurrentRequest {
    private String requestId;
    private Instant timestamp;
    private String client;
    private String currency;

    public String getRequestId() {
        return requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getClient() {
        return client;
    }

    public String getCurrency() {
        return currency;
    }
}
