package org.Users;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.persistence.*;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.images.Image;
import org.Search.*;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//import org.springframework.boot.security.*;
//import jakarta.persistence.OneToOne;


@Entity
@SelectBeforeUpdate
@DynamicUpdate
public class Profile {

    @Id
    private String username;
    private String name;
    private String bio;

    private String chatOpenTime;

    // will need implemented after posting
    // may need to be synced with the repository somehow
    // maybe change it to storing post IDs instead?
    @Column(columnDefinition = "TEXT")
    private ArrayList<Long> postIds = new ArrayList<>();

    // will need implemented after image posting
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image pfp;
    //private int pfpID;

    // will need implemented during chat feature
    // may need to be synced with a repository, could store chat IDs instead?
    private ArrayList<Object> chats = new ArrayList<>();

    private String lastOnline;

    private int online;

    //private Timeline timeline;

    private Profile() {}

    public Profile(String username, String name)
    {
        this.username = username;
        this.name = name;
        lastOnline = LocalDateTime.now().toString();
    }



    // Getters and setters

    public String getName() {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getBio()
    {
        return this.bio;
    }

    public void setBio(String bio)
    {
        this.bio = bio;
    }

    public ArrayList<Long> getPostIds()
    {
        return postIds;
    }

    public void setPostIds(ArrayList<Long> postIds)
    {
        this.postIds = postIds;
    }

    public ArrayList<Object> getChats()
    {
        return chats;
    }

    public void setChats(ArrayList<Object> chats)
    {
        this.chats = chats;
    }

    public Image getPfp()
    {
        return pfp;
    }

    public void setPfp(Image pfp)
    {
        this.pfp = pfp;
    }

/*
    public void updateOnline()
    {
        lastOnline = LocalDateTime.now().toString();
        online = 1;
    }
 */

    // something wrong with this
    public void goOnline()
    {
        lastOnline = LocalDateTime.now().toString();
        online = 1;
    }

    public void goOffline()
    {
        lastOnline = LocalDateTime.now().toString();
        online = 0;
    }

    public LocalDateTime getOnlineTime()
    {
        // online always is 0 for some reason
        if (online == 1)
        {
            return LocalDateTime.now();
        } else
        {
            return LocalDateTime.parse(lastOnline);
        }
    }


    public void updateDMOpenTime(String username)
    {
        JSONObject jo = new JSONObject(chatOpenTime);

        jo.remove(username);
        jo.put(username, LocalDateTime.now().toString());

        chatOpenTime = jo.toString();

    }

    public LocalDateTime getDMOpenTime(String username)
    {
        JSONObject jo = new JSONObject(chatOpenTime);

        return LocalDateTime.parse((String) jo.get(username));
    }

    public void updateChatOpenTime(int chatId)
    {
        JSONObject jo = new JSONObject(chatOpenTime);

        jo.remove(Integer.toString(chatId));
        jo.put(Integer.toString(chatId), LocalDateTime.now().toString());

        chatOpenTime = jo.toString();

    }

    public LocalDateTime getChatOpenTime(int chatId)
    {
        JSONObject jo = new JSONObject(chatOpenTime);
        LocalDateTime time = null;

        try
        {
            time = LocalDateTime.parse((String) jo.get(Integer.toString(chatId)));
        } catch (Exception e)
        {

        }

        return time;
    }


    public void setChatOpenTime(String thing)
    {
        chatOpenTime = thing;
    }



    /*
    public void setTimeline(Timeline timeline) { this.timeline = timeline; }

    public Timeline getTimeline() { return timeline; }
*/



    public void addPost(Long postId)
    {
        postIds.add(postId);
    }

    public void deletePost(Long postId)
    {
        postIds.remove(postId);
    }

    public void addChat(Object chat)
    {
        chats.add(chat);
    }

    public void removeChat(Object chat)
    {
        chats.remove(chat);
    }




}
