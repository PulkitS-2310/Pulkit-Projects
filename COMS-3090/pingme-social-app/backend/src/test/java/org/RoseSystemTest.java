package org;

import org.Search.Hashtag;
import org.Search.Post;
import org.Search.PostComparator;
import org.Search.PostRepository;
import org.Users.*;
import org.images.Image;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RoseSystemTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private TimelineRepository timelineRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        // Initialize MockMvc with the standalone controller configuration
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    public void testLoginCorrect() throws Exception
    {

        // could be static
        HashCreator hashCreator = new HashCreator();
        User testUser = new User("name", "username", "email", "password");

        String password = hashCreator.createSHAHash("password");
        User returnedUser = new User("name", "username","email",password);
        when(userRepository.findByUsername("username")).thenReturn(returnedUser);


        String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";


        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password correct"));


    }

    @Test
    public void testLoginIncorrect1() throws Exception
    {

        // could be static
        HashCreator hashCreator = new HashCreator();
        User testUser = new User("name", "username", "email", "password1");

        String password = hashCreator.createSHAHash("password1");
        User returnedUser = new User("name", "username","email",password);
        when(userRepository.findByUsername("username")).thenReturn(returnedUser);


        String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";


        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password Incorrect"));


    }

    @Test
    public void testLoginIncorrect2() throws Exception
    {

        
        // could be static
        HashCreator hashCreator = new HashCreator();
        User testUser = new User("name", "username", "email", "password1");

        String password = hashCreator.createSHAHash("password");
        User returnedUser = new User("name", "username","email",password);
        when(userRepository.findByUsername("username1")).thenReturn(null);

        String requestBody = "{\"username\":\"username1\",\"password\":\"password\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("No user with that login exists"));


    }

    @Test
    public void testChangePassword1() throws Exception
    {

        // could be static
        HashCreator hashCreator = new HashCreator();

        User testUser = new User("name", "username", "email", "password");

        String password = hashCreator.createSHAHash("password");
        User returnedUser = new User("name", "username","email", password);
        when(userRepository.findByUsername("username")).thenReturn(returnedUser);


        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });

        String requestBody = "{\"username\":\"username\",\"password\":\"" + "password" + "\",\"newPassword\":\"newPassword\"}";

        mockMvc.perform(post("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password successfully updated"));


    }

    @Test
    public void testChangePassword2() throws Exception
    {

        // could be static
        HashCreator hashCreator = new HashCreator();

        User testUser = new User("name", "username", "email", "password");

        String password = hashCreator.createSHAHash("password");
        User returnedUser = new User("name", "username","email", password);
        when(userRepository.findByUsername("username2")).thenReturn(null);

        String requestBody = "{\"username\":\"username2\",\"password\":\"" + password + "\",\"newPassword\":\"newPassword\"}";

        mockMvc.perform(post("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("No user found"));


    }

    @Test
    public void testChangePassword3() throws Exception
    {

        // could be static
        HashCreator hashCreator = new HashCreator();

        User testUser = new User("name", "username", "email", "password");

        String password = hashCreator.createSHAHash("password");
        User returnedUser = new User("name", "username","email", password);
        when(userRepository.findByUsername("username")).thenReturn(returnedUser);

        String requestBody = "{\"username\":\"username\",\"password\":\"" + "alsduifhajkf" + "\",\"newPassword\":\"newPassword\"}";

        mockMvc.perform(post("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password update failed"));


    }



    @Test
    public void testSignupEndpoint() throws Exception {
        // Setup test data
        User newUser = new User("name", "username", "email", "password");
        List<User> userList = new ArrayList<>();
        List<Profile> profileList = new ArrayList<>();
        List<Timeline> timelineList = new ArrayList<>();

        // Mock repository behavior
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    profileList.add(profile);
                    return profile;
                });

        when(timelineRepository.save(any(Timeline.class)))
                .thenAnswer(invocation -> {
                    Timeline timeline = invocation.getArgument(0);
                    timelineList.add(timeline);
                    return timeline;
                });

        when(userRepository.findByUsername("username")).thenReturn(null);

        // Create request body
        String requestBody = "{\"username\":\"username\",\"name\":\"name\",\"email\":\"email\",\"password\":\"password\"}";

        // Perform and verify the request
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User successfully signed up"));
    }




    @Test
    public void testGetUser() throws Exception {

        User newUser = new User("name", "username", "email", "password");

        when(userRepository.findByUsername("username")).thenReturn(newUser);

        String requestBody = "{\"username\":\"username\",\"name\":\"name\",\"email\":\"email\",\"password\":\"password\",\"friends\":[\"user\"],\"followers\":[\"user\"],\"following\":[\"user\"]}";

        mockMvc.perform(get("/users/{username}", "username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.friends").value(new ArrayList<>()));

    }


    @Test
    public void testUpdateUser() throws Exception {

        User newUser = new User("name1", "username1", "email1", HashCreator.createSHAHash("password"));
        Profile newProfile = new Profile("username1", "name1");
        Timeline newTimeline = new Timeline("username1");

        newUser.follow("friend");
        newUser.addFollower("friend");
        newUser.addFriend("friend");


        User updatedUser = new User("name", "username", "email", HashCreator.createSHAHash("password"));
        Profile updatedProfile = new Profile("username", "name");
        Timeline updatedTimeline = new Timeline("username");


        Post post = new Post();
        post.setId(Long.getLong("1"));
        /*
        post.getId();
        post.updateTime();
        post.getFormattedTime();
        post.getTimeRaw();
        post.setUsername("username");
        post.getUsername();
        post.setTitle("title");
        post.getTitle();
        Set<Hashtag> hashtags = new HashSet<Hashtag>();
        hashtags.add(new Hashtag("tag"));
        post.setHashtags(hashtags);
        post.getTagsString();
        post.setDescription("description");
        post.getDescription();
        post.toString();
         */


        newProfile.addPost(Long.getLong("1"));

        List<User> userList = new ArrayList<>();
        List<Profile> profileList = new ArrayList<>();
        List<Timeline> timelineList = new ArrayList<>();

        when(userRepository.findByUsername("username1")).thenReturn(newUser);
        when(profileRepository.findByUsername("username1")).thenReturn(newProfile);
        when(timelineRepository.findByUsername("username1")).thenReturn(newTimeline);
        when(userRepository.findByUsername("username")).thenReturn(updatedUser);
        //when(profileRepository.findByUsername("username")).thenReturn(updatedProfile);
        //when(timelineRepository.findByUsername("username")).thenReturn(updatedTimeline);
        when(postRepository.findById(Long.getLong("1"))).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("friend")).thenReturn(new User("name", "friend", "email", "password"));


        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    profileList.add(profile);
                    return profile;
                });

        when(timelineRepository.save(any(Timeline.class)))
                .thenAnswer(invocation -> {
                    Timeline timeline = invocation.getArgument(0);
                    timelineList.add(timeline);
                    return timeline;
                });



        String requestBody = "{\"username\":\"username\",\"name\":\"name\",\"email\":\"email\",\"password\":\"password\",\"friends\":[\"user\"],\"followers\":[\"user\"],\"following\":[\"user\"]}";


        mockMvc.perform(put("/users/{username}", "username1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email"));

    }



    @Test
    public void testDeleteUser() throws Exception {

        User user = new User("name", "username", "email",HashCreator.createSHAHash("password"));

        when(userRepository.findByUsername("username")).thenReturn(user);


        mockMvc.perform(delete("/users/{username}", "username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User deleted successfully"));



    }




    @Test
    public void testPosts()
    {


        Post post = new Post();
        post.setId(Long.getLong("1"));
        post.getId();
        post.updateTime();
        post.getFormattedTime();
        post.getTimeRaw();
        post.setUsername("username");
        post.getUsername();
        post.setTitle("title");
        post.getTitle();
        Set<Hashtag> hashtags = new HashSet<Hashtag>();
        hashtags.add(new Hashtag("tag"));
        post.setHashtags(hashtags);
        post.getTagsString();
        post.getHashtags();
        post.setDescription("description");
        post.getDescription();
        post.toString();

        Post post1 = new Post();
        post1.setId(Long.getLong("2"));
        post1.updateTime();

        PostComparator postComparator = new PostComparator();
        postComparator.compare(post, post);
        postComparator.compare(post, post1);
        postComparator.compare(post1, post);



    }


    @Test
    public void testUsers() throws NoSuchAlgorithmException {
        User user = new User();
        user = new User("name", "username","email","password");

        user.setName("name");
        user.setPassword("password");
        user.setUsername("username");
        user.setEmail("email");
        user.setFriends(new ArrayList<>());
        user.setFollowers(new ArrayList<>());
        user.setFollowing(new ArrayList<>());
        user.getName();
        user.getUsername();
        user.getPassword();
        user.getEmail();
        user.getFollowing();
        user.getFollowers();
        user.getFriends();
        user.addFollower("follower");
        user.addFriend("friend");
        user.follow("following");
        user.removeFollower("follower");
        user.removeFriend("friend");
        user.unfollow("following");


    }


    @Test
    public void testProfile()
    {

        Profile profile = new Profile("username", "name");
        profile.setChats(new ArrayList<>());
        profile.setPostIds(new ArrayList<>());
        profile.setUsername("username");
        profile.setName("name");
        profile.setBio("bio");
        profile.setPfp(new Image());
        profile.getBio();
        profile.getChats();
        profile.getName();
        profile.getPfp();
        profile.getUsername();
        profile.setChatOpenTime(new JSONObject().toString());
        profile.updateDMOpenTime("username");
        profile.goOnline();
        profile.goOffline();
        profile.getDMOpenTime("username");
        profile.updateChatOpenTime(1);
        profile.getChatOpenTime(1);
        profile.getOnlineTime();

        profile.addPost(Long.getLong("1"));
        profile.addChat(1);
        profile.removeChat(1);
        profile.deletePost(Long.getLong("1"));

    }



    @Test
    public void testFollow() throws Exception {

        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("following")).thenReturn(new User("name", "following", "email", "password"));

        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });


        mockMvc.perform(post("/users/user/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followingUsername\":\"following\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Followed successfully"));



    }


    @Test
    public void testUnfollow() throws Exception {

        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("following")).thenReturn(new User("name", "following", "email", "password"));

        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });


        mockMvc.perform(post("/users/user/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followingUsername\":\"following\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Following successfully ended"));



    }




    @Test
    public void testGetFollowing() throws Exception {

        User user = new User("name", "user", "email", "password");
        user.addFollower("following");
        User following = new User("name", "following", "email", "password");

        when(userRepository.findByUsername("user")).thenReturn(user);
        //when(userRepository.findByUsername("following")).thenReturn(following);

        List<User> userList = new ArrayList<>();


        ArrayList<User> followingList = new ArrayList<>();
        //followingList.add(following);

        mockMvc.perform(get("/users/user/following"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(followingList));



    }



    @Test
    public void testRemoveFollower() throws Exception {

        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("following")).thenReturn(new User("name", "following", "email", "password"));

        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });


        mockMvc.perform(post("/users/user/removeFollower")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerUsername\":\"following\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Follower successfully removed"));



    }


    @Test
    public void testRemoveFriend() throws Exception {

        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("following")).thenReturn(new User("name", "following", "email", "password"));

        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });


        mockMvc.perform(post("/users/user/removeFriend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"friendUsername\":\"following\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Friend successfully removed"));



    }



    @Test
    public void testaddFriend() throws Exception {

        when(userRepository.findByUsername("user")).thenReturn(new User("name", "user", "email", "password"));
        when(userRepository.findByUsername("following")).thenReturn(new User("name", "following", "email", "password"));

        List<User> userList = new ArrayList<>();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return user;
                });


        mockMvc.perform(post("/users/user/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"friendUsername\":\"following\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Friend successfully added"));



    }





}