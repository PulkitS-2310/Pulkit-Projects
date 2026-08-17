package com.example.androidexample;

// No external imports needed
public class TopPostItem {
    private final String title;
    private final String likedDate;

    public TopPostItem(String title, String likedDate) {
        this.title = title;
        this.likedDate = likedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getLikedDate() {
        return likedDate;
    }
}