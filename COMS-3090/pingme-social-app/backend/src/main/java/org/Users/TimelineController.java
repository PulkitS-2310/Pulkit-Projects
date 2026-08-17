package org.Users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.websocket.server.PathParam;
//import org.search.TweetComparator;
//import org.search.Tweet;
import org.Search.Post;
import org.Search.PostComparator;
import org.Search.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;



// should this be a websocket?
@RestController
@Component
public class TimelineController
{

    @Autowired
    UserRepository userRepository;

    @Autowired
    TimelineRepository timelineRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    PostRepository postRepository;

    /**
     * gets the timeline of a given user
     * @param username
     * @return Timeline
     */
    @Transactional
    @GetMapping(path = "/users/{username}/timeline")
    public Timeline getTimeline(@PathVariable String username)
    {
        return timelineRepository.findByUsername(username);
    }



    // refresh feed

    /**
     * refreshes the feed of a given user
     * @param username
     * @return Timeline
     */
    @GetMapping(path = "/users/{username}/refresh")
    @Transactional
    public Timeline refreshTimeline(@PathVariable String username)
    {
        User user = userRepository.findByUsername(username);
        Timeline timeline = timelineRepository.findByUsername(username);

        if (user == null || timeline == null)
        {
            return null;
        }
        // retrieve some number of posts from all people they are following and sort them by time posted

        ArrayList<Post> posts = new ArrayList<>();
        ArrayList<String> following = user.getFollowing();

        for (int i = 0; i < following.size(); i++)
        {
            String followingUsername = following.get(i);

            ArrayList<Post> followingPosts = new ArrayList<>();

            for (int k = 0; k < profileRepository.findByUsername(followingUsername).getPostIds().size(); k++)
            {
                Optional<Post> post = postRepository.findByIdWithHashtags(profileRepository.findByUsername(followingUsername).getPostIds().get(k));
                if (post.isPresent())
                {
                    //post.get().getHashtags().size();
                    followingPosts.add(post.get());
                }
            }

            // max posts from one user in the feed is currently 50 to prevent it from being too long
            for (int j = 0; j < Math.min(50, followingPosts.size()); j++)
            {
                posts.add(followingPosts.get(j));
            }

        }

        posts.sort(new PostComparator());

        timeline.setFeed(posts);
        timeline.updateLastRefreshed();

        //timelineRepository.deleteByUsername(username);
        timelineRepository.save(timeline);

        return timeline;
    }




    /**
     * returns every timeline for testing
     * @return List<Timeline>
     */
    @Transactional
    @GetMapping(path = "/allTimelines")
    public List<Timeline> getAllTimelines()
    {

        /*
        List<User> users = userRepository.findAll();

        for (int i = 0; i < users.size(); i++)
        {
            Timeline timeline = new Timeline(users.get(i).getUsername());
            timelineRepository.save(timeline);
        }
        */


        List<Timeline> timelines = timelineRepository.findAll();

        for (int i = 0; i < timelines.size(); i++)
        {
            Timeline timeline = timelines.get(i);
            timeline.updateLastRefreshed();
            //timeline.setFeed(new ArrayList<Post>());
        }


        return timelines;
    }

    /**
     * creates a new timeline for a user
     * @param username
     * @return Timeline
     */
    @PostMapping(path = "/users/{username}/timeline")
    public Timeline newTimeline(@PathVariable String username)
    {

        if (userRepository.findByUsername(username) != null)
        {
            Timeline timeline = new Timeline(username);
            timelineRepository.save(timeline);
            return timeline;
        }

        return null;
    }





}
