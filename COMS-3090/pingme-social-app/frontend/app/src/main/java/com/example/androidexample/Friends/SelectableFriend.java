package com.example.androidexample.Friends;

public class SelectableFriend {
    private String name;
    private String username; // Changed from email to username
    private boolean isSelected;

    public SelectableFriend(String name, String username) {
        this.name = name;
        this.username = username;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public String getUsername() { // Changed from getEmail to getUsername
        return username;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}