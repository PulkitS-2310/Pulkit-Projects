package org.Notifications;

import org.Chats.*;
import org.Search.PostComparator;
import org.Users.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.Search.Post;
import org.Search.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@Component
public class NotifController
{


    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    TimelineRepository timelineRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    DMRepository DMRepo;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MessageRepository MessageRepo;




    /**
     * needs to get all notifications for a given user, should check the states of all chats they are in if they were
     * updated since last message, as well as checking for posts from people they follow. This should maybe be able to be
     * specific to only certain users that they follow or something, same with chats.
     *
     * returns a JsonObject that's basically a map that has all the notifications stored in it.
     * @param username
     * @return String representation of a JSONObject
     */
    @GetMapping("/notifications/{username}")
    public String getNotifications(@PathVariable String username)
    {
        JSONObject notifs = new JSONObject();

        JSONObject postNotifs = new JSONObject();

        JSONObject chatNotifs = new JSONObject();

        Profile profile = profileRepository.findByUsername(username);
        if (profile == null) { return "Failure: Profile is null (username does not match any profile in database)"; }
        Timeline timeline = fetchTimeline(username);
        if (timeline == null) { return "Failure: Timeline is null (username does not match any timeline in database)"; }


        LocalDateTime time = LocalDateTime.now();

        // should start by checking the previous time the user was online and cleared the notifs
        // then only send new notifications from the time they were not in the app
        // or maybe send them until cleared? and they automatically clear in the app when in the different tabs like if
        // they refresh the timeline their post notifications go away and if they open a chat the notifs for that
        // chat go away.

        // Each user needs to have new variables associated with them, like for the timeline there should be a time that
        // it was last refreshed then it should track all posts that are new from the last time checked
        // also all chats need to have a new variable with them, probably in the profile have a map or something that
        // is all the ids and the time, then automatically run a check and check the times of chats sent
        // also needs to have each chat keep track of the time.



        // should be able to get the last timeline as well as a theoretical new timeline without refreshing
        // then add all the usernames and post titles into the notifications map


        // should probably have more done, this will probably always run but should have a buffer of like 1 minute or something

        if (timeline.getLastRefreshed().isBefore(time))
        {
            // needs to add notifications for posts

            for (int i = 0; i < timeline.getFeed().size(); i++)
            {
                Post post = timeline.getFeed().get(i);


                if (LocalDateTime.parse(post.getTimeRaw()).isAfter(timeline.getLastRefreshed()) )
                {
                    postNotifs.put(post.getUsername(), post.getTitle());
                }

            }

        }


        // should probably have more done, this will probably always run but should have a buffer of like 1 minute or something

        // will only be false if online == 1 (they are in a chat)
        //if (profile.getOnlineTime().isBefore(time))
        {
            // needs to add notifications for chats missed, needs to access all chats and compare all chat times



            List<Message> msgs = MessageRepo.findAll();

            ArrayList<Object> chats = profile.getChats();

            for (int i = 0; i < chats.size(); i++)
            {
                int groupID = Integer.parseInt(chats.get(i).toString());

                Group group = groupRepository.findGroupById(groupID);

                LocalDateTime lastMsg = null;

                // convert the list to a string
                StringBuilder sb = new StringBuilder();

                if(msgs != null && msgs.size() != 0) {
                    for (Message msg : msgs) {
                        // check that it is in this chat
                        if (msg.getGroupID() != null && msg.getGroupID() == groupID ) {
                            sb.append(msg.getUserName() + ": " + msg.getContent() + "\n");

                            lastMsg = msg.getSent();
                        }

                        //MessageRepo.delete(msg);
                        //msg.setSent(LocalDateTime.now());
                        //MessageRepo.save(msg);
                    }
                }

                String[] abcd = sb.toString().split("\n");

                String mostRecentChat = abcd[abcd.length - 1];


                if ( lastMsg != null && profile.getChatOpenTime(groupID) != null && profile.getChatOpenTime(groupID).isBefore(lastMsg) )
                {
                    chatNotifs.put(group.getGroupName(), mostRecentChat);
                }


            }







            // the following code adds the most recent DM message to the notifications if the DM was not opened before it was sent

            List<DM> DMs = DMRepo.findAll();
            User user = userRepository.findByUsername(username);

            LocalDateTime lastDM = null;

            ArrayList<String> friends = user.getFriends();
            for (int i = 0; i < friends.size(); i++)
            {
                String destUserName = friends.get(i);

                StringBuilder sb = new StringBuilder();
                if(DMs != null && DMs.size() != 0) {
                    for (DM dm : DMs) {
                        if (dm.getUserName().equals(destUserName)) {
                            sb.append(dm.getUserName() + ": " + dm.getContent() + "\n");


                            lastDM = dm.getSent();
                        }
                        if (dm.getUserName().equals(username))
                        {
                            sb.append(dm.getUserName() + ": " + dm.getContent() + "\n");

                            lastDM = dm.getSent();
                        }

                        //DMRepo.delete(dm);
                        //dm.setSent(LocalDateTime.now());
                        //DMRepo.save(dm);
                    }
                }

                String[] abcde = sb.toString().split("\n");

                String mostRecentDM = abcde[abcde.length - 1];

                if ( lastDM != null && profile.getDMOpenTime(destUserName) != null && profile.getDMOpenTime(destUserName).isBefore(lastDM) )
                {
                    chatNotifs.put(destUserName, mostRecentDM);
                }




            }








        }





        notifs.put("Posts", postNotifs);
        notifs.put("Chats", chatNotifs);


        return notifs.toString();
    }



    private Timeline fetchTimeline(@PathVariable String username)
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

        return timeline;
    }



}
