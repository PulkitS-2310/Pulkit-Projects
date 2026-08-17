package org.Analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET endpoints for retrieving analytics data
    @GetMapping
    public ResponseEntity<Analytics> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }

    @GetMapping("/users")
    public ResponseEntity<Analytics> getUserMetrics() {
        Analytics analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/activity")
    public ResponseEntity<Analytics> getActivityMetrics() {
        Analytics analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/engagement")
    public ResponseEntity<Analytics> getEngagementMetrics() {
        Analytics analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // POST endpoints for tracking events
    @PostMapping("/track/user/new")
    public ResponseEntity<Void> trackNewUser() {
        analyticsService.trackNewUser();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/user/active")
    public ResponseEntity<Void> trackActiveUser() {
        analyticsService.trackActiveUser();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/post/new")
    public ResponseEntity<Void> trackNewPost() {
        analyticsService.trackNewPost();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/comment/new")
    public ResponseEntity<Void> trackNewComment() {
        analyticsService.trackNewComment();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/like/new")
    public ResponseEntity<Void> trackNewLike() {
        analyticsService.trackNewLike();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/share/new")
    public ResponseEntity<Void> trackNewShare() {
        analyticsService.trackNewShare();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/session")
    public ResponseEntity<Void> trackSession(@RequestParam double durationInMinutes) {
        analyticsService.trackSession(durationInMinutes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/concurrent-users")
    public ResponseEntity<Void> updateConcurrentUsers(@RequestParam int currentUsers) {
        analyticsService.updateConcurrentUsers(currentUsers);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track/reset-daily")
    public ResponseEntity<Void> resetDailyMetrics() {
        analyticsService.resetDailyMetrics();
        return ResponseEntity.ok().build();
    }
}
