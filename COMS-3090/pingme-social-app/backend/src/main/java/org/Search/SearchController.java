package org.Search;

import org.Users.Profile;
import org.Users.UserRepository;
import org.Users.User;
import org.Users.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling search-related operations
 */
@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Search for users by username
     * @param username The search term to match against usernames
     * @return List of users whose usernames contain the search term
     */
    @GetMapping("/search/{username}")
    public ResponseEntity<List<User>> searchUsers(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ArrayList<>());
            }

            String searchQuery = username.toLowerCase().trim();
            List<User> matchingUsers = userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

            return ResponseEntity.ok(matchingUsers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ArrayList<>());
        }
    }

    /**
     * Search for posts by content
     * @param title The search term to match against post content
     * @return List of posts containing the search term
     */
    @GetMapping("/search/post/{title}")
    public ResponseEntity<List<Post>> searchPosts(@PathVariable String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ArrayList<>());
            }

            String searchQuery = title.toLowerCase().trim();
            List<Post> matchingPosts = postRepository.findByTitleContaining(searchQuery);
            return ResponseEntity.ok(matchingPosts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ArrayList<>());
        }
    }

    /**
     * Get a specific post by ID
     * @param id The ID of the post to retrieve
     * @return The requested post or 404 if not found
     */
    @GetMapping("/users/{username}/post/{id}")
    public ResponseEntity <Post> getPostById(@PathVariable Long id) {
        try {
            Post post = postRepository.findById(id).orElse(null);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/allPosts")
    public List<Post> getAllPosts()
    {
        List<Post> allPosts = postRepository.findAll();

        /*
        for (int i = 0; i < allPosts.size(); i++)
        {
            postRepository.deleteById(allPosts.get(i).getId());
        }
         */


        return allPosts;
    }

    @GetMapping("/users/{username}/posts")
    public List<Post> getPosts(@PathVariable String username) {

            List<Post> allPosts = postRepository.findAll();
            List<Post> userPosts = new ArrayList<>();
            Profile profile = profileRepository.findByUsername(username);
            ArrayList<Long> postIds = new ArrayList<>();

            for (int i = 0; i < allPosts.size(); i++)
            {
                if (allPosts.get(i).getUsername() != null && allPosts.get(i).getUsername().equals(username))
                {
                    userPosts.add(allPosts.get(i));
                    //postIds.add(allPosts.get(i).getId());
                }
            }

            //profile.setPostIds(postIds);
            //profileRepository.save(profile);


            return userPosts;

    }

    /**
     * Create a new post
     *
     * @param username The username of the post creator
     * @param post     The post data to create
     * @return The created post or error message
     */
    @PostMapping("/users/{username}/post")
    public String createPost(@PathVariable String username, @RequestBody Post post) {
        //try {
            // Validate the post data
            if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
                return "Post title cannot be empty";
            }

            /* Unneeded because you can get the profile from the username
            // Get the profile of the post creator
            Profile profile = profileRepository.findByUsername(username);
            if (profile == null) {
                return ResponseEntity.badRequest().body("User profile not found");
            }

            // Set the profile for the post
            post.setProfile(profile);

             */

            Profile profile = profileRepository.findByUsername(username);

            if (profile == null)
            {
                return "Failure, profile with that username does not exist";
            }

            // Set the current time for the post
            //TimeFormatter timeFormatter = new TimeFormatter();
            //post.setTimeUpload(timeFormatter.getFormatData());

            post.updateTime();

            post.setUsername(username);

            post.getHashtags();

            if (post.getFormattedTime() == null)
            {
                post.updateTime();
            }


            // Save the post
            Post savedPost = postRepository.save(post);


            profile.addPost(savedPost.getId());
            profileRepository.save(profile); // could not serialize exception


            /*
            // Create a response DTO to avoid circular references
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedPost.getId());
            response.put("title", savedPost.getTitle());
            response.put("hashtag", savedPost.getHashtags());
            response.put("description", savedPost.getDescription());
            response.put("timeUpload", savedPost.getTimeUpload());

            return ResponseEntity.ok(response);

             */
            return savedPost.toString();
            /*
        } catch (Exception e) {
            return "Failed to create post: " + e.getMessage();
        }*/
    }

    /**
     * Delete a post by ID
     * @param id The ID of the post to delete
     * @return Success or error message
     */
    @DeleteMapping("/users/{username}/post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        try {
            if (!postRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            postRepository.deleteById(id);
            return ResponseEntity.ok("Post successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete post: " + e.getMessage());
        }
    }
}
