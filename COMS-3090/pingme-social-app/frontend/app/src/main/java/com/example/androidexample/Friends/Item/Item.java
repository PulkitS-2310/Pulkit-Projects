package com.example.androidexample.Friends.Item;

import java.util.List;

public class Item {
    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_GROUP_CHAT = 2;

    private int type;
    private String name;
    private String username; // Changed from email to username
    private String chatId; // For group chats
    private List<String> groupUsernames; // For group chat members

    // Constructor for friend
    public Item(int type, String name, String username) {
        this(type, name, username, null, null);
    }

    // Constructor for group chat
    public Item(int type, String name, String chatId, List<String> groupUsernames) {
        this(type, name, null, chatId, groupUsernames);
    }

    private Item(int type, String name, String username, String chatId, List<String> groupUsernames) {
        this.type = type;
        this.name = name;
        this.username = username;
        this.chatId = chatId;
        this.groupUsernames = groupUsernames;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getChatId() {
        return chatId;
    }

    public List<String> getGroupUsernames() {
        return groupUsernames;
    }
}