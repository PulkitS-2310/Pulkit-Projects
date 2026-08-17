package org.Chats;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.antlr.v4.runtime.misc.NotNull;
import org.json.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//@Component
@Entity
@Table(name = "user_groups")
public class Group
{

    @Id
    @GeneratedValue
    @NotNull
    private long id;

    private String groupName;

    private ArrayList<String> usernames = new ArrayList<>();

    private String creator;

    private String nicknames;

    public Group()
    {
        creator = null;
    }

    /**
     * creates a new group with a creator
     * @param creator
     * @param creatorName
     */
    public Group(String creator, String creatorName)
    {
        usernames.add(creator);

        JSONObject jo = new JSONObject();
        jo.put(creator, creatorName);
        nicknames = jo.toString();

        this.creator = creator;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    /**
     * returns a Map<String, String> of the nicknames of all users with their usernames as keys
     * @return
     * @throws JsonProcessingException
     */
    public Map<String, Object> getNicknames() throws JsonProcessingException {
        //Map<String,String> result = new ObjectMapper().readValue(nicknames, Map.class);
        JSONObject result = new JSONObject(nicknames);

        return result.toMap();
    }

    /**
     * updates the nicknames based on given map
     * @param nicknames
     */
    public void setNicknames(Map<String, String> nicknames) {

        /*
        String formattedNicknames = nicknames.entrySet().stream()
                .map(entry -> '"' + entry.getKey() + '"' + ':' + '"' + entry.getValue() + '"')
                .collect(Collectors.joining(", ", "{", "}"));

       */

        JSONObject jo = new JSONObject(nicknames);

        this.nicknames = jo.toString();

    }

    public int getID() {
        return (int) id;
    }


    /**
     * updates just one nickname
     * @param username
     * @param nickname
     * @throws JsonProcessingException
     */
    public void setNickname(String username, String nickname) throws JsonProcessingException {
        //Map<String,String> result = new ObjectMapper().readValue(nicknames, Map.class);
        //result.put(username, nickname);

        //nicknames = result.toString();

        JSONObject result = new JSONObject(nicknames);
        result.put(username, nickname);
        nicknames = result.toString();


    }


    /**
     * adds a member to the chat
     * @param username
     * @throws JsonProcessingException
     */
    public void addMember(String username) throws JsonProcessingException {
        //Map<String,String> result = new ObjectMapper().readValue(nicknames, Map.class);

        JSONObject result = new JSONObject(nicknames);

        usernames.add(username);
        result.put(username, username);

        nicknames = result.toString();
    }

    /**
     * adds a member to the chat with a nickname
     * @param username
     * @param name
     * @throws JsonProcessingException
     */
    public void addMember(String username, String name) throws JsonProcessingException {
        //Map<String,String> result = new ObjectMapper().readValue(nicknames, Map.class);

        JSONObject result = new JSONObject(nicknames);

        usernames.add(username);

        if (name == null) {
            result.put(username, username);
        }
        else {
            result.put(username, name);
        }

        nicknames = result.toString();

    }

    /**
     * removes a member from the chat
     * @param username
     * @throws JsonProcessingException
     */
    public void removeMember(String username) throws JsonProcessingException {
        //Map<String,String> result = new ObjectMapper().readValue(nicknames, Map.class);

        usernames.remove(username);

        JSONObject result = new JSONObject(nicknames);
        result.remove(username);

        nicknames = result.toString();
    }

    /**
     * returns a string representation of this group formatted like a json object
     * @return String
     */
    @Override
    public String toString() {
        //ObjectMapper objectMapper = new ObjectMapper();

            // Create a map representation of the object
            //Map<String, Object> jsonMap = new HashMap<>();
            JSONObject jsonMap = new JSONObject();

            jsonMap.put("id", id);
            jsonMap.put("groupName", groupName);
            jsonMap.put("creator", creator);
            jsonMap.put("usernames", usernames);

            // Parse the nicknames string into a Map if it exists
            if (nicknames != null && !nicknames.isEmpty()) {

                    /*String formattedNicknames = nicknames.entrySet().stream()
                .map(entry -> '"' + entry.getKey() + '"' + ':' + '"' + entry.getValue() + '"')
                .collect(Collectors.joining(", ", "{", "}"));

                    jsonMap.put("nicknames", formattedNicknames);
                     */
                JSONObject jo = new JSONObject(nicknames);
                    jsonMap.put("nicknames", jo.toMap());

            } else {
                jsonMap.put("nicknames", new HashMap<>());
            }

            return jsonMap.toString();

    }




}
