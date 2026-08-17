package com.example.androidexample;

public class AnalyticsItem {
    private final String metric;
    private final String value;

    public AnalyticsItem(String metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }
}
