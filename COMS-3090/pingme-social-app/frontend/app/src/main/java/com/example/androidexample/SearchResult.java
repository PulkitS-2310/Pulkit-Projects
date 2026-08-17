package com.example.androidexample;

public class SearchResult {
    private String content;
    private String username;
    private String timestamp;
    private boolean isHashtag;

    public SearchResult(String content, String username, String timestamp, boolean isHashtag) {
        this.content = content;
        this.username = username;
        this.timestamp = timestamp;
        this.isHashtag = isHashtag;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isHashtag() {
        return isHashtag;
    }
} 