package com.example.androidexample.Search;

public class PostUser {
    private long id;
    private String title;
    private String description;
    private String hashtags;
    private String username;
    private String formattedTime;
    private String timeRaw;

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHashtags() {
        return hashtags;
    }

    public String getUsername() {
        return username;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getTimeRaw() {
        return timeRaw;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public void setTimeRaw(String timeRaw) {
        this.timeRaw = timeRaw;
    }
}
