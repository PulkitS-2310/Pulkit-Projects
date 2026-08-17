package org.Chats;

import jakarta.websocket.server.PathParam;
import org.Users.Profile;
import org.Users.ProfileController;
import org.Users.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Component
public class GroupController
{

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    DMRepository dmRepository;

    @GetMapping(path = "/getDMs")
    public List<DM> getAllDMs()
    {
        return dmRepository.findAll();
    }

    @GetMapping(path = "/group/{id}")
    public String getGroupById(@PathVariable String id)
    {
        if (id != null && groupRepository.findGroupById(Integer.parseInt(id)) != null)
        {
            return groupRepository.findGroupById(Integer.parseInt(id)).toString();
        } else {
            return "Error retrieving group";
        }

    }


    @GetMapping(path = "/allGroups")
    public String getAllGroups()
    {
        List<Group> groups = groupRepository.findAll();


        /*
        for (int i = 0; i < groups.size(); i++)
        {
            Group group = groups.get(i);
            ArrayList<String> usernames = group.getUsernames();

            for (int j = 0; j < usernames.size(); j++)
            {
                Profile profile = profileRepository.findByUsername(usernames.get(j));
                if (profile != null && profile.getChats() != null)
                {
                    profile.addChat(group.getID());
                }
                profileRepository.deleteByUsername(profile.getUsername());
                profileRepository.save(profile);

            }



        } */


        if (groups != null)
        {
            return groups.toString();
        } else {
            return null;
        }



    }


    @GetMapping(path = "/group")
    public String getGroupByNameAndCreator(@RequestBody Map<String, Object> info)
    {
        Group group = null;

        if (info.containsKey("groupId") && info.get("groupId") != null)
        {
            group = groupRepository.findGroupById( Integer.parseInt((String) info.get("groupId")) );
        } else {
            group = groupRepository.findGroupByGroupNameAndCreator((String) info.get("groupName"), (String) info.get("creator"));
        }

        if (group == null)
        {
            return null;
        }
        return group.toString();
    }

    @PostMapping(path = "/group")
            public String createGroup(@RequestBody Map<String, Object> info)
    {
        //needs null checking

        String groupName = (String) info.get("groupName");
        String username = (String) info.get("creator");

        if (groupRepository.findGroupByGroupNameAndCreator(groupName, username) != null)
        {
            return "{" + '"' + "Failure" + '"' + ":" + '"' + "Failure" + '"' + "}";
        }



        Group group = new Group(username, profileRepository.findByUsername(username).getName());
        group.setGroupName(groupName);

        if (info.containsKey("usernames") && info.get("usernames") != null && ( (ArrayList<String>) info.get("usernames")).contains(username) )
        {

            // needs null checking

            ArrayList<String> usernames = (ArrayList<String>) info.get("usernames");

            for (int i = 0; i < usernames.size(); i++)
            {
                if (profileRepository.findByUsername(usernames.get(i)) == null)
                {
                    usernames.remove(usernames.get(i));
                }
            }

            group.setUsernames(usernames);

            Map<String, String> newNicknames = new HashMap<>();

            for (int i = 0; i < usernames.size(); i++)
            {
                newNicknames.put(usernames.get(i), profileRepository.findByUsername(usernames.get(i)).getName());
            }

            group.setNicknames(newNicknames);

        }



        //Map<String, Object> nicknames = new HashMap<>();
        //nicknames.put(username, username);
        //group.setNicknames(nicknames);

        groupRepository.save(group);

        for (int i = 0; i < group.getUsernames().size(); i++)
        {
            Profile profile = profileRepository.findByUsername(group.getUsernames().get(i));
            profile.addChat(group.getID());
            profileRepository.save(profile);
        }


        //profileRepository.findByUsername(username).addChat(group.getID());

        return group.toString();
    }


    @PutMapping(path = "/group")
    public String updateGroup(@RequestBody Map<String, Object> info)
    {
        // needs null checking
        Group group = null;

        if (info.containsKey("groupName") && info.containsKey("creator")) {
            group = groupRepository.findGroupByGroupNameAndCreator((String) info.get("groupName"), (String) info.get("creator"));
        } else if (info.containsKey("groupId"))
        {
            group = groupRepository.findGroupById(Integer.parseInt((String) info.get("groupId")));
        }

        if (group == null)
        {
            return "{" + '"' + "Failure" + '"' + ":" + '"' + "Group is null, could not find group with that information" + '"' + "}";
        }

        if (info.containsKey("newGroupName") && info.get("newGroupName") != null)
        {
            group.setGroupName((String) info.get("newGroupName"));
        }

        if (info.containsKey("newCreator") && info.get("newCreator") != null)
        {
            group.setCreator((String) info.get("newCreator"));

            //profileRepository.findByUsername((String) info.get("creator")).removeChat(group.getID());

        }

        /*
        if (info.containsKey("nicknames") && info.get("nicknames") != null)
        {
            group.setNicknames((Map<String, String>) info.get("nicknames"));
        }
         */

        if (info.containsKey("usernames") && info.get("usernames") != null)
        {

            ArrayList<String> newUsernames = (ArrayList<String>) info.get("usernames");

            for (int i = 0; i < group.getUsernames().size(); i++)
            {
                if ( ! newUsernames.contains(group.getUsernames().get(i)) )
                {
                    Profile profile = profileRepository.findByUsername(group.getUsernames().get(i));
                    if (profile != null)
                    {
                        profile.removeChat(group.getID());
                    }
                }
            }

            for (int i = 0; i < newUsernames.size(); i++)
            {
                if ( ! group.getUsernames().contains(newUsernames.get(i)))
                {
                    Profile profile = profileRepository.findByUsername(newUsernames.get(i));
                    if (profile != null)
                    {
                        profile.addChat(group.getID());
                    }

                }
            }

            group.setUsernames(newUsernames);

            Map<String, String> newNicknames = new HashMap<>();

            for (int i = 0; i < newUsernames.size(); i++)
            {
                newNicknames.put(newUsernames.get(i), profileRepository.findByUsername(newUsernames.get(i)).getName());
            }

            group.setNicknames(newNicknames);


        }


            //groupRepository.deleteGroupById(group.getID());
            groupRepository.save(group);
            return groupRepository.findGroupByGroupNameAndCreator(group.getGroupName(), group.getCreator()).toString();
    }


    // delete mapping needed

    @DeleteMapping(path = "/group/{groupId}")
    public String deleteGroupById(@PathVariable String groupId)
    {

        Group group = groupRepository.findGroupById( Integer.parseInt((String) groupId));

        if (group == null)
        {
            return "{" + '"' + "Failure" + '"' + ":" + '"' + "Failure" + '"' + "}";
        }

        for (int i = 0; i < group.getUsernames().size(); i++)
        {
            Profile profile = profileRepository.findByUsername(group.getUsernames().get(i));
            if (profile != null) {
                profile.removeChat(group.getID());
            }
        }

        groupRepository.deleteGroupById( group.getID() );

        return "{" + '"' + "Success" + '"' + ":" + '"' + "Success" + '"' + "}";
    }



    @DeleteMapping(path = "/group")
    public String deleteGroup(@RequestBody Map<String, Object> info)
    {

        Group group = null;

        if (info.containsKey("groupId") && info.get("groupId") != null)
        {
            group = groupRepository.findGroupById( Integer.parseInt((String) info.get("groupId")));

            if (group == null)
            {
                return "{" + '"' + "Failure" + '"' + ":" + '"' + "Failure" + '"' + "}";
            }

            for (int i = 0; i < group.getUsernames().size(); i++)
            {
                Profile profile = profileRepository.findByUsername(group.getUsernames().get(i));
                if (profile != null) {
                    profile.removeChat(group.getID());
                }
            }

            groupRepository.deleteGroupById( Integer.parseInt((String) info.get("groupId")) );
        } else if (info.containsKey("groupName") && info.get("groupName") != null && info.containsKey("creator") && info.get("creator") != null)
        {
            group = groupRepository.findGroupByGroupNameAndCreator((String) info.get("groupName"), (String) info.get("creator"));

            if (group == null)
            {
                return "{" + '"' + "Failure" + '"' + ":" + '"' + "Failure" + '"' + "}";
            }

            for (int i = 0; i < group.getUsernames().size(); i++)
            {
                Profile profile = profileRepository.findByUsername(group.getUsernames().get(i));
                if (profile != null) {
                    profile.removeChat(group.getID());
                }
            }

            groupRepository.deleteGroupById( group.getID() );
        } else {
            return "{" + '"' + "Failure" + '"' + ":" + '"' + "Failure" + '"' + "}";
        }

        return "Success";
    }


}
