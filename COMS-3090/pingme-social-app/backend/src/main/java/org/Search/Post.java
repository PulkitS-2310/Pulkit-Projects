package org.Search;

import jakarta.persistence.*;
import org.Users.Profile;
import org.json.JSONObject;

import org.images.TimeFormatter;
import org.Search.Hashtag;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.String;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final long serialVersionUID = -2344964205458938202L;

    private String title;
    private String description;
    //private String timeUpload;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_hashtag",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    /* using the username instead, storing a duplicate profile is bad practice.
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

     */

    private String username;

    private String timeUploaded;

    private int imageID;

    public Post() {

    }

    // removed timeUpload variable, instead using the timeUploaded variable I wrote already

    public Post(String title, String description, Set<Hashtag> hashtags, String username) {
        this.title = title;
        this.description = description;
        this.hashtags = hashtags;
        this.username = username;
        imageID = -1;
        //this.timeUpload = timeUpload;
        timeUploaded = LocalDateTime.now().toString();
    }

    public Post(String title, String description, Set<Hashtag> hashtags, String username, int imageID) {
        this.title = title;
        this.description = description;
        this.hashtags = hashtags;
        this.username = username;
        this.imageID = imageID;
        //this.timeUpload = timeUpload;
        timeUploaded = LocalDateTime.now().toString();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageID(int imageID)
    {
        this.imageID = imageID;
    }

    public int getImageID()
    {
        return imageID;
    }


    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public ArrayList<String> getTagsString()
    {
        Object[] hashtagsList = hashtags.toArray();
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < hashtagsList.length; i++)
        {
            tags.add( ((Hashtag) hashtagsList[i]).toString() );
        }
        return tags;
    }


    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    /*
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

     */

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getTimeRaw()
    {
        return timeUploaded;
    }

    public String getFormattedTime()
    {
        LocalDateTime time = LocalDateTime.parse(timeUploaded);
        DateTimeFormatter customTime = DateTimeFormatter.ofPattern("HH:mm 'T' MM-dd-yyyy");
        return time.format(customTime);
    }

    public void updateTime()
    {
        timeUploaded = LocalDateTime.now().toString();
    }


/*
    public String getTimeUpload() {
        TimeFormatter time = new TimeFormatter();
        return time.getFormatData();
    }

    public void setTimeUpload(String timeUpload) {
        this.timeUpload = timeUpload;
    }

 */

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("title", title);
        jo.put("description", description);
        jo.put("id", id);
        jo.put("imageID", imageID);
        jo.put("hashtags", hashtags);
        jo.put("tagsString", this.getTagsString());
        //jo.put("timeUploaded", getFormattedTime());
        jo.put("formattedTime", this.getFormattedTime());
        jo.put("timeRaw", this.getTimeRaw());

        return jo.toString();
    }
}
