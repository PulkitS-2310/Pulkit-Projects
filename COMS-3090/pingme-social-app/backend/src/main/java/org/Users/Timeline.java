package org.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
//import org.search.Post;
//import org.search.Tweet;
//import org.search.Tweet;
import org.Search.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
public class Timeline
{

    @Id
    private String username;

    //@Type(JsonType.class)
    @Column(columnDefinition = "LONGTEXT")
    private ArrayList<Post> postFeed;

    private String lastRefreshed;

    public Timeline()
    {

    }

    public Timeline(String username)
    {
        this.username = username;
        lastRefreshed = LocalDateTime.now().toString();
    }


    public void setFeed(ArrayList<Post> newFeed)
    {
        // needs to go through all the people they are following and retrieve the posts and put them into the postFeed
        // variable in chronological order

        //ArrayList<Post> newFeed = new ArrayList<>();


        // may need to be done in the TimelineController class instead of here, for now this will just be a set function

        this.postFeed = newFeed;
        //lastRefreshed = LocalDateTime.now().toString();

    }

    @Transactional
    public ArrayList<Post> getFeed(){ return postFeed; }



    public LocalDateTime getLastRefreshed()
    {
        return LocalDateTime.parse(lastRefreshed);
    }

    public void updateLastRefreshed()
    {
        lastRefreshed = LocalDateTime.now().toString();
    }


    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }




}
