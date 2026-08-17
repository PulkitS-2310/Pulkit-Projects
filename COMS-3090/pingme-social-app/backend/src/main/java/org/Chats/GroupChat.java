package org.Chats;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.Search.Post;
import org.Search.PostRepository;
import org.Users.Profile;
import org.Users.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;



import java.io.IOException;
import java.util.*;


// will eventually have to include sending posts

@Controller
@ServerEndpoint("/groupChat/{username}")
@Component
public class GroupChat
{

    private static MessageRepository MessageRepo;

    private static GroupRepository groupRepository;

    private static ProfileRepository profileRepository;

    private static PostRepository postRepository;

    @Autowired
    public void setPostRepository(PostRepository repo) {
        postRepository = repo;  // we are setting the static variable
    }

    @Autowired
    public void setProfileRepository(ProfileRepository repo) {
        profileRepository = repo;  // we are setting the static variable
    }

    @Autowired
    public void setGroupRepository(GroupRepository repo) {
        groupRepository = repo;  // we are setting the static variable
    }


    @Autowired
    public void setMessageRepository(MessageRepository repo) {
        MessageRepo = repo;  // we are setting the static variable
    }

    private static Map < Session, String > sessionUsernameMap = new Hashtable < > ();
    private static Map < String, Session > usernameSessionMap = new Hashtable < > ();
    private static Map < Session, Long > sessionChatMap = new Hashtable < > ();

    private int lastChatID;

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(Chat.class);

    /**
     * sets up a session when opened
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
     * does all chat commands and makes sure that all messages go to the right chats
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

            //String destUserName = split_msg[0].substring(1);    //@username and get rid of @
            int destChatID = Integer.parseInt(split_msg[0].substring(1));

            Long destChatIDLong = Long.parseLong(split_msg[0].substring(1));    //@ID and get rid of @
            if (sessionChatMap.get(session) == null)
            {
                sessionChatMap.put(session, destChatIDLong);
            } else
            {
                sessionChatMap.remove(session);
                sessionChatMap.put(session, destChatIDLong);
            }

            String postID = split_msg[1].substring(5);

            Optional<Post> post = postRepository.findByIdWithHashtags(Long.parseLong(postID));
            String data = "";

            if (post.isPresent())
            {
                data = post.get().toString();
            }


            Group group = groupRepository.findGroupById(destChatID);

            if (group != null && group.getUsernames().contains(username)) {

                //Map<String, String> nicknames = group.getNicknames();

                ArrayList<String> users = group.getUsernames();

                for (int i = 0; i < users.size(); i++) {
                    if (usernameSessionMap.get(users.get(i)) != null) {
                        Map<String, Object> nicknames = group.getNicknames();
                        String nickname = (String) nicknames.get(username);

                        sendMessageToPArticularUser(users.get(i), "@" + destChatID + " " + nickname + ": " + data);
                        logger.info("Sent message to " + users.get(i) + ":" + username + "/" + nickname + ": " + data);
                    }

                }

                MessageRepo.save(new Message(username, data, group.getID()));
                logger.info("Stored Message");

            }





            Profile profile = profileRepository.findByUsername(username);
            profile.updateChatOpenTime(destChatID);
            profileRepository.save(profile);

            lastChatID = destChatID;


        } else if (message.contains("/getChats"))
        {
            // split by space
            String[] split_msg =  message.split("\\s+");


            int destChatID = Integer.parseInt(split_msg[0].substring(1));    //@ID and get rid of @



            Long destChatIDLong = Long.parseLong(split_msg[0].substring(1));    //@ID and get rid of @
            if (sessionChatMap.get(session) == null)
            {
                sessionChatMap.put(session, destChatIDLong);
            } else
            {
                sessionChatMap.remove(session);
                sessionChatMap.put(session, destChatIDLong);
            }



            List<Message> msgs = MessageRepo.findAll();

            Group group = groupRepository.findGroupById(destChatID);


            //Map<String, String> nicknames = group.getNicknames();

            if (group != null && group.getUsernames().contains(username)) {
                Map<String, Object> nicknames = group.getNicknames();

                StringBuilder sb = new StringBuilder();
                if (msgs != null && msgs.size() != 0) {
                    for (Message msg : msgs) {
                        if (msg.getGroupID() != null && msg.getGroupID() == destChatID && msg.getContent() != null) {
                            String nickname = (String) nicknames.get(msg.getUserName());
                            sb.append("@" + msg.getGroupID() + " " + nickname + ": " + msg.getContent() + "\n");
                        }
                    }
                }

                sendMessageToPArticularUser(username, sb.toString());

                Profile profile = profileRepository.findByUsername(username);
                profile.updateChatOpenTime(destChatID);
                profileRepository.save(profile);

                lastChatID = destChatID;
            }

        }

        else if (message.startsWith("@")) {

            // split by space
            String[] split_msg =  message.split("\\s+");

            // Combine the rest of message
            StringBuilder actualMessageBuilder = new StringBuilder();
            for (int i = 1; i < split_msg.length; i++) {
                actualMessageBuilder.append(split_msg[i]).append(" ");
            }
            int destChatID = Integer.parseInt(split_msg[0].substring(1));    //@username and get rid of @



            Long destChatIDLong = Long.parseLong( (split_msg[0].substring(1)) );    //@ID and get rid of @
            if (sessionChatMap.get(session) == null)
            {
                sessionChatMap.put(session, destChatIDLong);
            } else
            {
                sessionChatMap.remove(session);
                sessionChatMap.put(session, destChatIDLong);
            }



            String actualMessage = actualMessageBuilder.toString();

            Group group = groupRepository.findGroupById(destChatID);

            if (group != null && group.getUsernames().contains(username)) {

                //Map<String, String> nicknames = group.getNicknames();

                ArrayList<String> users = group.getUsernames();

                for (int i = 0; i < users.size(); i++) {
                    if (usernameSessionMap.get(users.get(i)) != null) {
                        Map<String, Object> nicknames = group.getNicknames();
                        String nickname = (String) nicknames.get(username);

                        sendMessageToPArticularUser(users.get(i), "@" + destChatID + " " + nickname + ": " + actualMessage);
                        logger.info("Sent message to " + users.get(i) + ":" + username + "/" + nickname + ": " + actualMessage);
                    }

                }

                MessageRepo.save(new Message(username, actualMessage, group.getID()));
                logger.info("Stored Message");

            }

            Profile profile = profileRepository.findByUsername(username);
            profile.updateChatOpenTime(destChatID);
            profileRepository.save(profile);

            lastChatID = destChatID;

        }





    }


    /**
     * handles closing a chat
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
        profile.updateChatOpenTime(lastChatID);
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
        profile.updateChatOpenTime(lastChatID);
        profileRepository.save(profile);

        // do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    /**
     * sends a message to just one user
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
     * gets the chat history for a given group
     * @param groupID
     * @return
     */
    private String getChatHistory(int groupID) {
        List<Message> msgs = MessageRepo.findAll();

        Group group = groupRepository.findGroupById(groupID);

        // convert the list to a string
        StringBuilder sb = new StringBuilder();

        if(msgs != null && msgs.size() != 0) {
            for (Message msg : msgs) {
                // check that it is in this chat
                if ( msg.getGroupID() == groupID ) {
                    sb.append(msg.getUserName() + ": " + msg.getContent() + "\n");
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
