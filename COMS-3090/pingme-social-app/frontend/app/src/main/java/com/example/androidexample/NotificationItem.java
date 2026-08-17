package com.example.androidexample;

// simple POJO to hold one notification
public class NotificationItem {
    private final String title;
    private final String body;
    private final long timestamp;

    public NotificationItem(String title, String body, long timestamp) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
