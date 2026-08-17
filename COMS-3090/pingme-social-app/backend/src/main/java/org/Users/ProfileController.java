package org.Users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import org.search.Tweet;
import org.Search.Post;
import org.Search.PostComparator;
import org.Search.PostRepository;
import org.images.Image;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


@RestController
@Component
public class ProfileController
{

    // will be needed since i'm planning to link the profile to the user and not have it in it's own repository
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    PostRepository postRepository;

    /**
     * gets all profiles
     * @return List<Profile>
     */
    @GetMapping(path = "/profiles")
    List<Profile> getUserProfiles()
    {
        List<Profile> allProfiles = profileRepository.findAll();


        for (int i = 0; i < allProfiles.size(); i++)
        {
            Profile profile = allProfiles.get(i);
            //profile.updateOnline();
            profile.goOnline();
            profile.goOffline();
            //profile.setChatOpenTime(new JSONObject().toString());

            /*
            ArrayList<Object> chats = profile.getChats();
            for (int k = 0; k < chats.size(); k++)
            {
                profile.updateChatOpenTime(chats.get(k));
            }
             */

            /*
            ArrayList<String> friends = userRepository.findByUsername(profile.getUsername()).getFriends();
            for (int k = 0; k < friends.size(); k++)
            {
                profile.updateDMOpenTime(friends.get(k));
            }
             */


            /*
            if (profile.getChats() == null)
            {
                //profile.setChats(new ArrayList<Object>());
            } else {
                ArrayList<Object> chats = profile.getChats();

                for (int j = 0; j < chats.size(); j++)
                {
                    Integer chatId = (Integer) chats.get(j);
                    if (chatId == 0)
                    {
                        chats.remove(chats.get(j));
                    }
                }

            }

             */

            /*
            if (profile.getName() == null)
            {
                profile.setName(profile.getUsername());
            }

             */
            /*
            User user = userRepository.findByUsername(profile.getUsername());
            if (user.getName() == null || !profile.getName().equals(user.getName()))
            {
                user.setName(profile.getName());
            }
            userRepository.save(user);
             */


            List<Post> allPosts = postRepository.findAll();
            List<Post> userPosts = new ArrayList<>();
            //Profile profile = profileRepository.findByUsername(username);
            ArrayList<Long> postIds = new ArrayList<>();

            for (int l = 0; l < allPosts.size(); l++)
            {
                if (allPosts.get(l).getUsername() != null && allPosts.get(l).getUsername().equals(profile.getUsername()))
                {
                    userPosts.add(allPosts.get(l));
                    postIds.add(allPosts.get(l).getId());
                }
            }

            profile.setPostIds(postIds);


            //profileRepository.deleteByUsername(profile.getUsername());
            profileRepository.save(profile);


        }

        allProfiles = profileRepository.findAll();

        return allProfiles;
    }

    /**
     * returns a given user's profile
     * @param username
     * @return Profile
     */
    @GetMapping(path = "/users/{username}/profile")
    Profile getUserProfile(@PathVariable String username)
    {
        User user = userRepository.findByUsername(username);
        Profile profile = profileRepository.findByUsername(username);
        if (user == null)
            return null;

        if (profile == null)
            return null;

        if (profile.getChats() == null)
        {
            profile.setChats(new ArrayList<>());
        }

        if (profile.getPostIds() == null)
        {
            profile.setPostIds(new ArrayList<>());
        }
        
        return profile;
    }


    /**
     * updates a profile with given information in the input Map<String, String> with the keys "name", "bio", "chats", "pfp", "username", "posts"
     * @param username
     * @param info
     * @return Profile
     */
    @PutMapping(path = "/users/{username}/profile")
    Profile updateUserProfile(@PathVariable String username, @RequestBody Map<String, Object> info)
    {
        User user = userRepository.findByUsername(username);
        Profile profile = profileRepository.findByUsername(username);

        if (user == null)
            return null;

        if (profile == null)
            return null;

        // Sets the profile name to the name of the user automatically if they are different
        /*
        if (user.getName() != null && !user.getName().equals(profile.getName()) )
        {
            user.setName(profile.getName());
        } else
         */if (user.getName() == null && profile.getName() != null)
        {
            user.setName(profile.getName());
        }



        if (info.containsKey("name"))
        {
            user.setName((String) info.get("name"));
            // updates profile name automatically
            profile.setName((String) info.get("name"));
        }

        if (info.containsKey("bio") && info.get("bio") != null /*&& profile.getBio() != null && !profile.getBio().equals((String) info.get("bio")) */)
        {
            profile.setBio((String) info.get("bio"));
        }

        if (info.containsKey("posts") && info.get("posts") != null)
        {
            // may need to do a long comparison of all posts like with friends list

            Post[] incomingPosts = (Post[]) ((List<Post>) info.get("posts")).toArray();

            ArrayList<Post> newPosts = new ArrayList<>();

            // add all current posts
            for (int i = 0; i < profile.getPostIds().size(); i++)
            {
                newPosts.add(postRepository.findById(profile.getPostIds().get(i)).get());
            }

            for (int i = 0; i < incomingPosts.length; i++)
            {
                if ( (newPosts.lastIndexOf(incomingPosts[i]) == -1) ) // does not contain
                {
                    Post newPost = postRepository.save(incomingPosts[i]);
                    newPosts.add(newPost);
                } else
                { // maybe needs to update posts that are the same
                    //newPosts.add( profile.getPosts().get(profile.getPosts().lastIndexOf(incomingPosts[i])) );
                }

                // should it delete posts? I'm going with no, use a delete mapping.

            }

            ArrayList<Long> newPostIds = new ArrayList<>();

            for (int i =0; i < newPosts.size(); i++)
            {
                newPostIds.add(newPosts.get(i).getId());
            }

            profile.setPostIds(newPostIds);
            //profileRepository.save(profile);

        }

        if (info.containsKey("pfp")) {
            profile.setPfp(((Image) info.get("pfp")));
        }

        if (info.containsKey("chats"))
        {
            // may need to do a long comparison of all posts like with friends list

            profile.setChats( (ArrayList<Object>) info.get("chats"));

        }

        if (info.containsKey("username") && !user.getUsername().equals((String) info.get("username")))
        {

            profileRepository.deleteByUsername(username);
            profile.setUsername((String) info.get("username"));
            profileRepository.save(profile);


            userRepository.deleteByUsername(user.getUsername());
            user.setUsername((String) info.get("username"));
            userRepository.save(user);

            // updates profile username automatically
        } else
        {
            userRepository.save(user);
        }

        //profileRepository.deleteByUsername(username);
/*
        ArrayList<Long> ids = profile.getPostIds();
        ArrayList<Post> posts = new ArrayList<>();
        for (int l = 0; l < ids.size(); l++)
        {
            if (postRepository.findById(ids.get(l)).isPresent())
            {
                posts.add(postRepository.findById(ids.get(l)).get());
            }
        }

        posts.sort(new PostComparator());

        ids = new ArrayList<>();
        for (int l = 0; l < posts.size(); l++)
        {
            ids.add(posts.get(l).getId());
        }
        profile.setPostIds(ids);

 */

        profileRepository.save(profile);

        return profile;
    }

    /**
     * creates a new profile for a given user, input is a Map<String, String> that has the keys "name", "pfp", "bio", "postIds", "chats"
     * @param username
     * @param info
     * @return Profile
     */
    @PostMapping(path = "/users/{username}/profile")
    public Profile createProfile(@PathVariable String username, @RequestBody Map<String, Object> info)
    {

        User user = userRepository.findByUsername(username);

        if (user == null)
        {
            return null;
        }

        Profile profile = new Profile(username, user.getName());

        if (info.containsKey("name"))
        {
            profile.setName((String) info.get("name"));
        }

        if (info.containsKey("pfp")) {
            profile.setPfp(((Image) info.get("pfp")));
        }

        if (info.containsKey("bio")) {
            profile.setBio(((String) info.get("bio")));
        }

        if (info.containsKey("postIds")) {
            profile.setPostIds((ArrayList<Long>) info.get("postIds"));
        }

        if (info.containsKey("chats")) {
            profile.setChats((ArrayList<Object>) info.get("chats"));
        }

        profileRepository.save(profile);


        return profile;
    }



/*
    @PostMapping(path = "/users/{username}/profile/newPost")
    public Post createPost(@PathVariable String username, Map<String, Object> info)
    {

        Profile profile = profileRepository.findByUsername(username);

        if (profile == null || info.get("post") == null)
        {
            return null;
        }

        profile.addPost((Post) info.get("post"));

        profileRepository.save(profile);

        return (Post) info.get("post");

    }


 */



}
