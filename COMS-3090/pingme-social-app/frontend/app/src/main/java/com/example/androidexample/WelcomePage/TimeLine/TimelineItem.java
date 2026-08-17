package com.example.androidexample.WelcomePage.TimeLine;

import java.util.Date;

public class TimelineItem {
    private final long id;
    private final String title;
    private final String description;
    private final String username;
    private final String formattedTime;

    private String userName;
    private String imageUrl;
    private String postContent;
    private Date timestamp;

    public TimelineItem(long id, String title, String description, String username, String formattedTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.formattedTime = formattedTime;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostContent() {
        return postContent;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

