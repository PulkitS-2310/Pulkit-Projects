package org.Users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.Search.Post;
import org.Search.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


/**
 * There needs to be more work done to connect the profile, timeline, and user classes. when a user is deleted it should
 * delete their profile and timeline too, but neither of those should be deleted on their own.
 * Each needs to have its own table since they cant store them nested without issues, which requires each to have a
 * controller and repository class. they should be able to be all made at once when a new user is created and they should
 * be linked by the username (and with profile the name as well) so that must be updated when the user is updated here too.
 * also timeline has a following variable
 *
 */








@RestController
@Component
public class UserController {

/*
    public UserController(main.java.HashCreator HashCreator) {
        //this.HashCreator = HashCreator;
    }
    */

    //@Bean
    //HashCreator HashCreator = new HashCreator();

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    TimelineRepository timelineRepository;

    @Autowired
    PostRepository postRepository;


    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    /**
     * returns a list of all users
     * @return List<User>
     */
    @GetMapping(path = "/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    /**
     * lets a user sign up. input is a user object to be stored in the database.
     * @param user
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/signup")
    String createUser(@RequestBody User user) throws NoSuchAlgorithmException {
        if (user == null || user.getUsername() == null)
            return "User information incorrect";
        user.setPassword(HashCreator.createSHAHash(user.getPassword()));

        if (userRepository.findByUsername(user.getUsername()) != null)
            return "User with username already exists";
        userRepository.save(user);

        Profile profile = new Profile(user.getUsername(), user.getName());
        profileRepository.save(profile);

        Timeline timeline = new Timeline(user.getUsername());
        timelineRepository.save(timeline);

        return "User successfully signed up";
    }


    /**
     * lets a user login. checks their password and username are correct, given by inputting a Map<String, String>
     *     where it uses the keys "username" and "password"
     * @param info
     * @return String with description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/login")
    String login(@RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        String username = info.get("username");
        String email = info.get("email");
        User user;
        if (email == null && username != null) {
            user = userRepository.findByUsername(username);
        } else {
            user = userRepository.findByEmail(email);
        }
        if (user == null)
            return "No user with that login exists";

        String password = info.get("password");


        if (password == null)
            return "No password provided";

        //check passwords
        if (MessageDigest.isEqual(HashCreator.createSHAHash(password).getBytes(), user.getPassword().getBytes())) {
            // needs to create a token

            System.out.println("Login success");
            return "Password correct";
        } else {
            System.out.println("Login failure");
            return "Password Incorrect";
        }
    }


    /**
     * gets a single user, input is a String of the username given in the path
     * @param username
     * @return User
     */
    @GetMapping(path = "/users/{username}")
    User getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }




    /**
     * updates a user and any information stored about them if new information is provided. input is the username and a Map<String, String>
     *     that can contain the keys "username", "password", "email", "name", "friends", "followers", "following". if the username given in the
     *     map is different it updates the user's username to that. Returns the updated user.
     * @param username
     * @param info
     * @return User
     * @throws NoSuchAlgorithmException
     */
    @PutMapping("/users/{username}")
    User updateUser(@PathVariable String username, @RequestBody Map<String, Object> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            return null;

        Profile profile = profileRepository.findByUsername(username);
        //if (profile == null)
            //return null;

        if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
            return null;


        User request = new User((String) info.get("name"), (String) info.get("username"), (String) info.get("email"), (String) info.get("password"));

        // update friends if there is a provided list
        if (info.containsKey("friends")) {
            if (!user.getFriends().equals(info.get("friends"))) {
                // needs to compare them and update the other users accordingly
                List<String> original = user.getFriends().stream().toList();
                int originalIndex = 0;
                List<String> updated = ((ArrayList<String>) info.get("friends")).stream().toList();
                int updatedIndex = 0;
                ArrayList<String> complete = new ArrayList<>();
                int loopVar = 0;

                while (originalIndex < original.size() || updatedIndex < updated.size()) {
                    // check if there are any friends that are not in the original then add them
                    /*
                    if (updatedIndex < updated.size() && !original.contains(updated.get(updatedIndex))) {
                        // needs to add for the other user too maybe? unless that is bad
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    } */
                    // check if any friends are the same
                    if (updatedIndex < updated.size() && original.contains(updated.get(updatedIndex))) {
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    }
                    // check if any friends were removed
                    if (originalIndex < original.size() && !updated.contains(original.get(originalIndex))) {
                        // must remove them from their friend's list as well
                        User friend = userRepository.findByUsername(original.get(originalIndex));
                        friend.removeFriend(user.getUsername());
                        originalIndex++;
                    }
                    loopVar++;
                    if (loopVar > original.size() + updated.size() + 1)
                    {
                        break;
                    }
                }
                user.setFriends(complete);
            }
        } else
        {
            // retain original list
        }
        // update followers if there is a provided list
        if (info.containsKey("followers")) {
            if (!user.getFollowers().equals(info.get("followers"))) {
                // needs to compare them and update the other users accordingly
                List<String> original = user.getFollowers().stream().toList();
                int originalIndex = 0;
                List<String> updated = ((ArrayList<String>) info.get("followers")).stream().toList();
                int updatedIndex = 0;
                ArrayList<String> complete = new ArrayList<>();
                int loopVar = 0;

                while (originalIndex < original.size() || updatedIndex < updated.size()) {
                    // check if there are any followers that are not in the original then add them
                    /*
                    if (updatedIndex < updated.size() && !original.contains(updated.get(updatedIndex))) {
                        // needs to add for the other user too maybe? unless that is bad
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    } */
                    // check if any followers are the same
                    if (updatedIndex < updated.size() && original.contains(updated.get(updatedIndex))) {
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    }
                    // check if any followers were removed
                    if (originalIndex < original.size() && !updated.contains(original.get(originalIndex))) {
                        // must remove them from their friend's list as well
                        User follower = userRepository.findByUsername(original.get(originalIndex));
                        follower.unfollow(user.getUsername());
                        originalIndex++;
                    }
                    loopVar++;
                    if (loopVar > original.size() + updated.size() + 1)
                    {
                        break;
                    }
                }
                user.setFollowers(complete);

            }
        } else
        {
            // retain original list
        }
        // update following if there is a provided list
        if (info.containsKey("following")) {
            if (!user.getFollowing().equals(info.get("following"))) {
                // needs to compare them and update the other users accordingly
                List<String> original = user.getFollowing().stream().toList();
                int originalIndex = 0;
                List<String> updated = ((ArrayList<String>) info.get("following")).stream().toList();
                int updatedIndex = 0;
                ArrayList<String> complete = new ArrayList<>();
                int loopVar = 0;

                while (originalIndex < original.size() || updatedIndex < updated.size()) {
                    // check if there are any accounts they are following that are not in the original then add them

                    if (updatedIndex < updated.size() && !original.contains(updated.get(updatedIndex))) {
                        // needs to add for the other user too because this does not require them to follow back
                        User following = userRepository.findByUsername(updated.get(updatedIndex));
                        following.addFollower(user.getUsername());
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    }
                    // check if any accounts they are following are the same
                    if (updatedIndex < updated.size() && original.contains(updated.get(updatedIndex))) {
                        complete.add(updated.get(updatedIndex));
                        updatedIndex++;
                    }
                    // check if any accounts they are following were removed
                    if (originalIndex < original.size() && !updated.contains(original.get(originalIndex))) {
                        // must remove them from their following list as well
                        User following = userRepository.findByUsername(original.get(originalIndex));
                        following.removeFollower(user.getUsername());
                        originalIndex++;
                    }
                    loopVar++;
                    if (loopVar > original.size() + updated.size() + 1)
                    {
                        break;
                    }
                }
                user.setFollowing(complete);

                // timeline needs to update the following too. or do that in timeline controller by just requesting every time ig

            }
        } else
        {
            // retain original list
        }

        // update the standard information
        if (user.getName() == null || !user.getName().equals(request.getName())) {
            user.setName(request.getName());
            profile.setName(request.getName());
        }
        if (!user.getEmail().equals(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (!user.getPassword().equals( HashCreator.createSHAHash(request.getPassword())) ) {
            user.setPassword(HashCreator.createSHAHash(request.getPassword()));
            //user.setPassword(request.getPassword());
        }


        userRepository.save(user);

        profileRepository.save(profile);


        /*
         *
         *   needs to update the user's profile username as well!!
         *
         */

        if (!user.getUsername().equals(request.getUsername())) {
            // needs to make sure to update ALL the information, this is not enough
            // mostly the usernames that the followers and friends have stored.
            userRepository.deleteByUsername(user.getUsername());
            user.setUsername(request.getUsername());
            userRepository.save(user);

            // may have an issue with deleting the user so comment it out
            //profileRepository.deleteByUsername(username);
            profile.setUsername(user.getUsername());
            profileRepository.save(profile);

            List<Post> posts = new ArrayList<>();

            for (int i = 0; i < profile.getPostIds().size(); i++)
            {
                posts.add(postRepository.findById(profile.getPostIds().get(i)).get());
            }

            for (int i = 0; i < posts.size(); i++)
            {
                Post post = posts.get(i);
                post.setUsername(profile.getUsername());
                postRepository.save(post);
                // might need to delete then save again idk
            }


            Timeline timeline = timelineRepository.findByUsername(username);
            timelineRepository.deleteByUsername(username);
            timeline.setUsername(user.getUsername());
            timelineRepository.save(timeline);


        }

        return userRepository.findByUsername(user.getUsername());
    }



    /**
     * Deletes a user based on their username and password. Input is the username in the path and a Map<String, String> that contains
     * the key "password" with the password.
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @DeleteMapping(path = "/users/{username}")
    String deleteUser(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        if (userRepository.findByUsername(username) == null)
        {
            return "User does not exist";
        }
        if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            return "Password incorrect";
        }
        userRepository.deleteByUsername(username);
        profileRepository.deleteByUsername(username);
        timelineRepository.deleteByUsername(username);
        return "User deleted successfully";
    }

    /**
     * Returns a given user's friends list
     * @return ArrayList<User>
     */
    @GetMapping(path = "/users/{username}/friends")
    ArrayList<User> getFriendsByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            ArrayList<User> friends = new ArrayList<>();
            for(int i = 0; i < user.getFriends().size(); i++)
            {
                friends.add(userRepository.findByUsername(user.getFriends().get(i)));
            }
            return friends;
        } else {
            return null;
        }
    }


    /**
     * adds two users as friends, input is the username of this user and a Map<String, String> that contains the keys
     * "friendUsername" and "password"
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/users/{username}/friends")
    String addFriendByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        String friendUsername = info.get("friendUsername");
        if (userRepository.findByUsername(username) == null)
        {
            return "User does not exist";
        }
        if (MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            return "Password incorrect";
        }

        User user = userRepository.findByUsername(username);
        User friend = userRepository.findByUsername(friendUsername);
        if (friend != null && user != null) {
            //user.addFriend(friendUsername);
            user.addFollower(friendUsername);
            user.follow(friendUsername);
            userRepository.deleteByUsername(username);
            userRepository.save(user);
            //friend.addFriend(username);
            friend.addFollower(username);
            friend.follow(username);
            userRepository.deleteByUsername(friendUsername);
            userRepository.save(friend);

            return "Friend successfully added";
        } else {
            return "Error: User is null";
        }


    }


    /**
     * Removes two users as friends by making them both unfollow each other
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/users/{username}/removeFriend")
    String deleteFriendByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        String friendUsername = info.get("friendUsername");
        User friend = userRepository.findByUsername(friendUsername);
        if (user == null)
        {
            return "User does not exist";
        }
        //if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            //return "Password incorrect";
        }

        if (friend != null) {
            user.removeFollower(friendUsername);
            user.unfollow(friendUsername);
            userRepository.deleteByUsername(username);
            userRepository.save(user);
            friend.removeFollower(username);
            friend.unfollow(username);
            userRepository.deleteByUsername(friendUsername);
            userRepository.save(friend);

            return "Friend successfully removed";
        } else {
            return "Error: User is null";
        }


    }

    /**
     * returns list of followers of a given user
     * @param username
     * @return ArrayList<User>
     */
    @GetMapping(path = "/users/{username}/followers")
    ArrayList<User> getFollowersByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            ArrayList<User> followers = new ArrayList<>();
            for(int i = 0; i < user.getFollowers().size(); i++)
            {
                followers.add(userRepository.findByUsername(user.getFollowers().get(i)));
            }
            return followers;
        } else {
            return null;
        }


    }


    // unneeded functionality
    // gonna add it anyways
    /**
     * unneeded functionality
     */
    /*
    @PostMapping(path = "/users/{username}/followers")
    String addFollowerByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        String followerUsername = info.get("followerUsername");
        User follower = userRepository.findByUsername(followerUsername);
        if (user == null)
        {
            return "User does not exist";
        }
        if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            return "Password incorrect";
        }

        if (follower != null)
        {
            user.addFollower(followerUsername);
            follower.follow(username);

            userRepository.deleteByUsername(username);
            userRepository.save(user);

            userRepository.deleteByUsername(followerUsername);
            userRepository.save(follower);

            return "Follower added successfully";
        } else
        {
            return "Error: User is null";
        }
    }
*/


    /**
     * removes a follower, input is a Map<String, String> with the keys "friendUsername" and "password"
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/users/{username}/removeFollower")
    String deleteFollowerByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        String followerUsername = info.get("followerUsername");
        User follower = userRepository.findByUsername(followerUsername);
        if (user == null)
        {
            return "User does not exist";
        }
        //if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            //return "Password incorrect";
        }

        if (followerUsername != null && userRepository.findByUsername(followerUsername) != null) {
            user.removeFollower(followerUsername);
            userRepository.deleteByUsername(username);
            userRepository.save(user);
            follower.unfollow(username);
            userRepository.deleteByUsername(followerUsername);
            userRepository.save(follower);

            return "Follower successfully removed";
        } else {
            return "Error: User is null";
        }
    }


    /**
     * returns list of people a given user is following
     * @param username
     * @return ArrayList<User>
     */
    @GetMapping(path = "/users/{username}/following")
    ArrayList<User> getFollowingByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            ArrayList<User> following = new ArrayList<>();
            for(int i = 0; i < user.getFollowing().size(); i++)
            {
                following.add(userRepository.findByUsername(user.getFollowing().get(i)));
            }
            return following;
        } else {
            return null;
        }


    }


    /**
     * makes this user follow a given user, input is a Map<String, String> with the keys "followingUsername" and "password"
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/users/{username}/follow")
    String followByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        String followingUsername = info.get("followingUsername");
        User followingUser = userRepository.findByUsername(followingUsername);
        if (user == null)
        {
            return "User does not exist";
        }
        /*
        if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            return "Password incorrect";
        }

         */

        if (followingUser != null) {
            user.follow(followingUsername);
            followingUser.addFollower(username);

            userRepository.deleteByUsername(username);
            userRepository.save(user);

            userRepository.deleteByUsername(followingUsername);
            userRepository.save(followingUser);

            // update timeline maybe

            return "Followed successfully";
        } else {
            return "Error: User is null";
        }


    }

    /**
     * this user stops following a given user, input is a Map<String, String> with the keys "followingUsername" and "password"
     * @param username
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/users/{username}/unfollow")
    String unfollowByUsername(@PathVariable String username, @RequestBody Map<String, String> info) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        String followingUsername = info.get("followingUsername");
        User followingUser = userRepository.findByUsername(followingUsername);
        if (user == null)
        {
            return "User does not exist";
        }
        //if (!MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            //return "Password incorrect";
        }

        if (followingUser != null) {
            user.unfollow(followingUsername);
            followingUser.removeFollower(username);

            userRepository.deleteByUsername(username);
            userRepository.save(user);

            userRepository.deleteByUsername(followingUsername);
            userRepository.save(followingUser);

            // update timeline maybe

            return "Following successfully ended";
        } else {
            return "Error: User is null";
        }


    }





/*
    // update this maybe?
    @PostMapping(path = "/users/{username}/validate")
    String validateUser(@PathVariable String username, @RequestBody Map<String, String> password) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(username);
        if (password == null || user == null)
            return failure;

        //check passwords
        if (MessageDigest.isEqual(password.get("password").getBytes(), user.getPassword().getBytes()))
        {
            return "Password correct";
        } else
        {
            return "Password Incorrect";
        }


    } */

    /**
     * updates a user's password. input is a Map<String, String> with the keys "username", "password", "newPassword"
     * @param info
     * @return A string description of the success or failure
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(path = "/user/change-password")
    String updatePassword(@RequestBody Map<String, String> info) throws NoSuchAlgorithmException {

        String username = info.get("username");
        String password = info.get("password");
        String newPassword = info.get("newPassword");
        User user = userRepository.findByUsername(username);

        if (user == null)
            return "No user found";
        if (password == null || newPassword == null)
            return "Password is null";

        // check password first
        if (MessageDigest.isEqual(( HashCreator.createSHAHash( (String) info.get("password"))).getBytes(), userRepository.findByUsername(username).getPassword().getBytes()))
        {
            // then set it
            user.setPassword(HashCreator.createSHAHash(newPassword));
            userRepository.deleteByUsername(username);
            userRepository.save(user);
            return "Password successfully updated";

        } else {
            return "Password update failed";
        }
    }


    /**
     * unimplemented feature that would send an email to reset the password if forgotten
     * @param info
     * @return
     */
    /*
    @PostMapping(path = "/forgot-password")
    String forgotPassword(@RequestBody Map<String, String> info) {
        String username = info.get("username");
        String email = info.get("email");
        User user = userRepository.findByUsername(username);

        if (user == null)
            return "User not found";
        if (email == null)
            return "Email is null";

        // how to validate? usually it sends an email


        return "";
    }
    */



}
