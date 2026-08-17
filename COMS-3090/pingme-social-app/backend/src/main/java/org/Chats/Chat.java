package org.Chats;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.Search.Post;
import org.Search.PostRepository;
import org.Users.Profile;
import org.Users.ProfileRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;



import java.io.IOException;
import java.util.*;


// will eventually have to include sending posts

@Controller
@ServerEndpoint("/chat/{username}")
@Component
public class Chat
{

    private static DMRepository DMRepo;

    private static ProfileRepository profileRepository;

    private static PostRepository postRepository;

    @Autowired
    public void setProfileRepository(ProfileRepository repo) {
        profileRepository = repo;  // we are setting the static variable
    }

    @Autowired
    public void setPostRepository(PostRepository repo) {
        postRepository = repo;  // we are setting the static variable
    }


    @Autowired
    public void setMessageRepository(DMRepository repo) {
        DMRepo = repo;  // we are setting the static variable
    }

    private static Map < Session, String > sessionUsernameMap = new Hashtable < > ();
    private static Map < String, Session > usernameSessionMap = new Hashtable < > ();
    private static Map < Session, String > sessionDMMap = new Hashtable < > ();

    private String lastDMUsername;

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(Chat.class);

    /**
     * handles opening a new session
     * @param session
     * @param username
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {


        // server side log
        logger.info("[onOpen] " + username);

        // map current session with username
        sessionUsernameMap.put(session, username);

        // map current username with session
        usernameSessionMap.put(username, session);

        Profile profile = profileRepository.findByUsername(username);
        profile.goOnline();
        profileRepository.save(profile);

        // send the whole chat
        //sendMessageToPArticularUser(username, getChatHistory(username, friend));
        // should use a command maybe? that includes the username and get the frontend to send it


        // temp
        //sendMessageToPArticularUser(username, "Joined the chat");

        //broadcast("test");

    }


    /**
     * does all chat commands and makes sure they all go to the correct DM
     * @param session
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        if (message.contains("POST:"))
        {
            String[] split_msg =  message.split("\\s+");

            String destUserName = split_msg[0].substring(1);    //@username and get rid of @
            String postID = split_msg[1].substring(5);

            Optional<Post> post = postRepository.findByIdWithHashtags(Long.parseLong(postID));
            String data = "";

            if (post.isPresent())
            {
                data = post.get().toString();
            }

            if (usernameSessionMap.get(destUserName) != null)
            {
                // format could use updating
                sendMessageToPArticularUser(destUserName, username + ": " + data);
                logger.info("Sent DM to " + destUserName + ":" + username + ": " + data);
            }
            sendMessageToPArticularUser(username, username + ": " + data);
            DMRepo.save(new DM(username, destUserName, data));
            logger.info("Stored DM");


            Profile profile = profileRepository.findByUsername(username);
            profile.updateDMOpenTime(destUserName);
            profileRepository.save(profile);

            lastDMUsername = destUserName;


        } else if (message.contains("/getChats"))
        {
            // split by space
            String[] split_msg =  message.split("\\s+");


            String destUserName = split_msg[0].substring(1);    //@username and get rid of @




            List<DM> DMs = DMRepo.findAll();


            StringBuilder sb = new StringBuilder();
            if(DMs != null && DMs.size() != 0) {
                for (DM dm : DMs) {
                    if (dm.getUserName().equals(destUserName)) {
                        sb.append(dm.getUserName() + ": " + dm.getContent() + "\n");
                    }
                    if (dm.getUserName().equals(username))
                    {
                        sb.append(dm.getUserName() + ": " + dm.getContent() + "\n");

                    }
                }
            }

            sendMessageToPArticularUser(username, sb.toString());

            Profile profile = profileRepository.findByUsername(username);
            profile.updateDMOpenTime(destUserName);
            profileRepository.save(profile);

            lastDMUsername = destUserName;

        }
        // should always start with that if it's a DM
        else if (message.startsWith("@")) {

            // split by space
            String[] split_msg =  message.split("\\s+");

            // Combine the rest of message
            StringBuilder actualMessageBuilder = new StringBuilder();
            for (int i = 1; i < split_msg.length; i++) {
                actualMessageBuilder.append(split_msg[i]).append(" ");
            }
            String destUserName = split_msg[0].substring(1);    //@username and get rid of @

            String actualMessage = actualMessageBuilder.toString();

            if (usernameSessionMap.get(destUserName) != null)
            {
                // format could use updating
                sendMessageToPArticularUser(destUserName, username + ": " + actualMessage);
                logger.info("Sent DM to " + destUserName + ":" + username + ": " + actualMessage);

            }
            sendMessageToPArticularUser(username, username + ": " + actualMessage);
            DMRepo.save(new DM(username, destUserName, actualMessage));
            logger.info("Stored DM");

            Profile profile = profileRepository.findByUsername(username);
            profile.updateDMOpenTime(destUserName);
            profileRepository.save(profile);


        }





    }


    /**
     * handles closing a session
     * @param session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // server side log
        logger.info("[onClose] " + username);



        Profile profile = profileRepository.findByUsername(sessionUsernameMap.get(session));
        profile.goOffline();
        profile.updateDMOpenTime(lastDMUsername);
        profileRepository.save(profile);

        // remove user from memory mappings
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

    }


    /**
     * logs errors
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        // get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        Profile profile = profileRepository.findByUsername(sessionUsernameMap.get(session));
        profile.goOffline();
        profile.updateDMOpenTime(lastDMUsername);
        profileRepository.save(profile);


        // do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }


    /**
     * sends message to a single user
     * @param username
     * @param message
     */
    private void sendMessageToPArticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }


    /**
     * returns the chat history between two users
     * @param username
     * @param friendUser
     * @return
     */
    private String getChatHistory(String username, String friendUser) {
        List<DM> DMS = DMRepo.findAll();

        // convert the list to a string
        StringBuilder sb = new StringBuilder();

        if(DMS != null && DMS.size() != 0) {
            for (DM DM : DMS) {
                // check that it is in this chat
                if ( (DM.getFriend().equals(friendUser) && DM.getUserName().equals(username)) ) {
                    sb.append(DM.getUserName() + ": " + DM.getContent() + "\n");
                }
                // from either user
                if ((DM.getUserName().equals(friendUser) && DM.getFriend().equals(username)))
                {
                    sb.append(DM.getFriend() + ": " + DM.getContent() + "\n");
                }
            }
        }
        return sb.toString();
    }




//    private void broadcast(String message) {
//        sessionUsernameMap.forEach((session, username) -> {
//            try {
//                session.getBasicRemote().sendText(message);
//            } catch (IOException e) {
//                logger.info("[Broadcast Exception] " + e.getMessage());
//            }
//        });
//    }





}
