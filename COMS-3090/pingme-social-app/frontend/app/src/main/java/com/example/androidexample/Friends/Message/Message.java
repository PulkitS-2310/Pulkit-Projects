package com.example.androidexample.Friends.Message;

public class Message {
    private String content;
    private String type; // e.g., "welcome", "dm", "system"

    public Message(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}