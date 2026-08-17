package org;

import jakarta.inject.Inject;
import org.Analytics.AnalyticsController;
import org.Analytics.AnalyticsRepository;
import org.Analytics.Analytics;
import org.Analytics.AnalyticsService;
import org.Commenting.CommentRepository;
import org.Commenting.Comment;
import org.Commenting.CommentController;
import org.Search.PostRepository;
import org.images.ImageController;
import org.images.ImageRepository;
import org.images.Image;
import org.images.TimeFormatter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AdomaleSystemTest {

    private MockMvc mockMvc;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private TimeFormatter timeFormatter;

    @InjectMocks
    private ImageController imageController;

    @InjectMocks
    private CommentController commentController;

    @InjectMocks
    private AnalyticsController analyticsController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController, commentController, analyticsController).build();
        objectMapper = new ObjectMapper();
        
        /*
         Mock TimeFormatter behavior
         when(timeFormatter.getFormatData()).thenReturn("2024-03-21 12:00:00");
         */

        // Mock AnalyticsService behavior
        Analytics mockAnalytics = new Analytics();
        mockAnalytics.setId(1L);
        mockAnalytics.setTotalUsers(100);
        mockAnalytics.setActiveUsers(50);
        mockAnalytics.setNewUsersToday(10);
        mockAnalytics.setTotalPosts(200);
        mockAnalytics.setTotalComments(500);
        mockAnalytics.setTotalLikes(1000);
        mockAnalytics.setTotalShares(100);
        mockAnalytics.setAverageSessionDuration(15.5);
        mockAnalytics.setTotalSessions(1000);
        mockAnalytics.setPeakConcurrentUsers(75);
        
        //when(analyticsService.getAnalytics()).thenReturn(mockAnalytics);
    }

    //Test #1
//    @Test
//    public void uploadImageTest() throws Exception {
//        // Create a mock image file
//        MockMultipartFile mockFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "test image content".getBytes());
//
//        // Create a mock Image object that would be returned by the repository
//        Image mockImage = new Image();
//        mockImage.setId(1);
//        mockImage.setImageName("test-image.jpg");
//        mockImage.setImageSize(mockFile.getBytes().length);
//        mockImage.setUploadTime("2024-05-07 12:00:00");
//
//        // Mock the repository behavior
//        when(imageRepository.save(any(Image.class))).thenReturn(mockImage);
//
//        // Perform the test
//        mockMvc.perform(post(MockMvcRequestBuilders.multipart("/images/upload")
//                        .contentType()
//                        .content())
//                .file(mockFile))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.imageName").value("test-image.jpg"))
//                .andExpect(jsonPath("$.imageSize").value(mockFile.getBytes().length));
//    }

    // Test #2
    @Test
    public void gettingImageTest() throws Exception {
        // Create a mock image
        Image mockImage = new Image();
        mockImage.setId(2);
        mockImage.setImageName("test-image.jpg");
        mockImage.setImageSize(1024);
        mockImage.setUploadTime("2025-05-07 12:00:00");

        // Mock the repository behavior
        when(imageRepository.findById(2)).thenReturn(mockImage);

        // Perform the test using GET method
        mockMvc.perform(MockMvcRequestBuilders.get("/images/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.imageName").value("test-image.jpg"))
                .andExpect(jsonPath("$.imageSize").value(1024))
                .andExpect(jsonPath("$.uploadTime").value("2025-05-07 12:00:00"));
    }

    // Test #3
    @Test
    public void deleteImageTest() throws Exception {
        // Test successful deletion
        //when(imageRepository.findById(1)).thenReturn(new Image());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Image successfully deleted"));

        // Test deletion of non-existent image (should still return 200 as per controller implementation)
        //when(imageRepository.findById(999)).thenReturn(null);
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/images/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Image successfully deleted"));
    }

    // Test #4
    @Test
    public void commentingTest() throws Exception {
        // Test creating a comment
        Comment testComment = new Comment();
        testComment.setUsername("testUser");
        testComment.setContent("This is a test comment");
        testComment.setTimestamp("2024-03-21 12:00:00");

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        mockMvc.perform(MockMvcRequestBuilders.post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.content").value("This is a test comment"));

        // Test getting all comments
        List<Comment> comments = Arrays.asList(testComment);
        when(commentRepository.findAll()).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/comment/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment"));

        // Test getting comment by ID
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        mockMvc.perform(MockMvcRequestBuilders.get("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.content").value("This is a test comment"));

        // Test getting comments by username
        when(commentRepository.findByUsername("testUser")).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/comment/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment"));

        // Test searching comments
        when(commentRepository.findByContentContaining("test")).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/comment/search")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment"));

        // Test updating a comment
        Comment updatedComment = new Comment();
        updatedComment.setUsername("testUser");
        updatedComment.setContent("This is an updated comment");
        updatedComment.setTimestamp("2024-03-21 12:30:00");

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        mockMvc.perform(MockMvcRequestBuilders.put("/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is an updated comment"));

        // Test deleting a comment
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        mockMvc.perform(MockMvcRequestBuilders.delete("/comment/1"))
                .andExpect(status().isOk());
    }

    // Test #5
    @Test
    public void analyticsTest() throws Exception {
        // Create a mock Analytics object
        Analytics mockAnalytics = new Analytics();
        mockAnalytics.setId(1L);
        mockAnalytics.setTotalUsers(100);
        mockAnalytics.setActiveUsers(50);
        mockAnalytics.setNewUsersToday(10);
        mockAnalytics.setTotalPosts(200);
        mockAnalytics.setTotalComments(500);
        mockAnalytics.setTotalLikes(1000);
        mockAnalytics.setTotalShares(100);
        mockAnalytics.setAverageSessionDuration(15.5);
        mockAnalytics.setTotalSessions(1000);
        mockAnalytics.setPeakConcurrentUsers(75);

        // Mock the service behavior
        when(analyticsService.getAnalytics()).thenReturn(mockAnalytics);

        // Test getting all analytics
        mockMvc.perform(MockMvcRequestBuilders.get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeUsers").value(50))
                .andExpect(jsonPath("$.newUsersToday").value(10));

        // Test getting user metrics
        mockMvc.perform(MockMvcRequestBuilders.get("/analytics/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeUsers").value(50))
                .andExpect(jsonPath("$.newUsersToday").value(10));

        // Test getting activity metrics
        mockMvc.perform(MockMvcRequestBuilders.get("/analytics/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(200))
                .andExpect(jsonPath("$.totalComments").value(500))
                .andExpect(jsonPath("$.totalLikes").value(1000))
                .andExpect(jsonPath("$.totalShares").value(100));

        // Test getting engagement metrics
        mockMvc.perform(MockMvcRequestBuilders.get("/analytics/engagement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSessionDuration").value(15.5))
                .andExpect(jsonPath("$.totalSessions").value(1000))
                .andExpect(jsonPath("$.peakConcurrentUsers").value(75));

        // Test tracking new user
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/user/new"))
                .andExpect(status().isOk());

        // Test tracking active user
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/user/active"))
                .andExpect(status().isOk());

        // Test tracking new post
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/post/new"))
                .andExpect(status().isOk());

        // Test tracking new comment
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/comment/new"))
                .andExpect(status().isOk());

        // Test tracking new like
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/like/new"))
                .andExpect(status().isOk());

        // Test tracking new share
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/share/new"))
                .andExpect(status().isOk());

        // Test tracking session
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/session")
                .param("durationInMinutes", "20.5"))
                .andExpect(status().isOk());

        // Test updating concurrent users
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/concurrent-users")
                .param("currentUsers", "80"))
                .andExpect(status().isOk());

        // Test resetting daily metrics
        mockMvc.perform(MockMvcRequestBuilders.post("/analytics/track/reset-daily"))
                .andExpect(status().isOk());
    }
}
