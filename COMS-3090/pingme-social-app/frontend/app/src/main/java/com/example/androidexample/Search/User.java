package com.example.androidexample.Search;

import java.util.List;

public class User {
    private String name;
    private String username;
    private String email;
    private List<String> followers;
    private List<String> following;
    private String password;
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    // Getters
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<String> getFollowers() { return followers; }
    public List<String> getFollowing() { return following; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setFollowers(List<String> followers) { this.followers = followers; }
    public void setFollowing(List<String> following) { this.following = following; }
}
