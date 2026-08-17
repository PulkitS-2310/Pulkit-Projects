package org.Users;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

//import org.springframework.boot.security.*;
//import jakarta.persistence.OneToOne;


@Entity
@SelectBeforeUpdate
@DynamicUpdate
public class User
{

 //   @Id
 //   @GeneratedValue(strategy = GenerationType.IDENTITY)
 //   private int id;
    private String name;
    @Id
    private String username;
    private String email;
    private String password;

    // needs more work, stubs
    //private Profile profile;

    //@OneToMany
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();

    /**
     * basic constructor for a user. name and email can be null if neccessary.
     * @param name
     * @param username
     * @param email
     * @param password
     * @throws NoSuchAlgorithmException
     */
    public User(String name, String username, String email, String password) throws NoSuchAlgorithmException {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;

        //profile = new Profile(username, name);
    }

    public User() {}

    // =============================== Getters and Setters for each field ================================== //

    /*
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    */

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
        //profile.setName(name);
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
        //profile.setUsername(username);
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    // should already be hashed
    public void setPassword(String newPassword)
    {
        this.password = newPassword;
    }

/*
    public Profile getProfile()
    {
        return profile;
    }

    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

    public Timeline getTimeline()
    {
        return profile.getTimeline();
    }

    public void setTimeline(Timeline timeline)
    {
        profile.setTimeline(timeline);
    }
*/


    // Friends list stuff

    public ArrayList<String> getFriends()
    {
        return friends;
    }

    public ArrayList<String> getFollowers()
    {
        return followers;
    }

    public ArrayList<String> getFollowing()
    {
        return following;
    }





    public void addFriend(String friendUsername)
    {
        if (!friends.contains(friendUsername))
        {
            friends.add(friendUsername);
        }
    }

    public void removeFriend(String friendUsername)
    {
        friends.remove(friendUsername);
    }



    public void addFollower(String followerUsername)
    {
        if (!followers.contains(followerUsername))
        {
            followers.add(followerUsername);
        }
    }

    public void removeFollower(String followerUsername)
    {
        followers.remove(followerUsername);
        friends.remove(followerUsername);
    }



    public void follow(String toFollowUsername)
    {
        if (!following.contains(toFollowUsername))
        {
            following.add(toFollowUsername);
        }

        if (followers.contains(toFollowUsername) && !friends.contains(toFollowUsername))
        {
            friends.add(toFollowUsername);
        }
    }

    public void unfollow(String toUnfollowUsername)
    {
        following.remove(toUnfollowUsername);
        friends.remove(toUnfollowUsername);
    }


    public void setFriends(ArrayList<String> updatedFriends)
    {
        friends.clear();
        List<String> list = updatedFriends.stream().toList();
        friends.addAll(list);
    }

    public void setFollowing(ArrayList<String> updatedFollowing)
    {
        following.clear();
        List<String> list = updatedFollowing.stream().toList();
        following.addAll(list);
    }

    public void setFollowers(ArrayList<String> updatedFollowers)
    {
        followers.clear();
        List<String> list = updatedFollowers.stream().toList();
        followers.addAll(list);
    }








}
