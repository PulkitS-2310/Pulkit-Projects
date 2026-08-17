package org.Analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    
    @Autowired
    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }
    
    @Transactional
    public Analytics getAnalytics() {
        return analyticsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    Analytics analytics = new Analytics();
                    return analyticsRepository.save(analytics);
                });
    }
    
    @Transactional
    public void trackNewUser() {
        Analytics analytics = getAnalytics();
        analytics.incrementTotalUsers();
        analytics.incrementNewUsersToday();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackActiveUser() {
        Analytics analytics = getAnalytics();
        analytics.incrementActiveUsers();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackNewPost() {
        Analytics analytics = getAnalytics();
        analytics.incrementTotalPosts();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackNewComment() {
        Analytics analytics = getAnalytics();
        analytics.incrementTotalComments();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackNewLike() {
        Analytics analytics = getAnalytics();
        analytics.incrementTotalLikes();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackNewShare() {
        Analytics analytics = getAnalytics();
        analytics.incrementTotalShares();
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void trackSession(double durationInMinutes) {
        Analytics analytics = getAnalytics();
        analytics.updateSessionMetrics(durationInMinutes);
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void updateConcurrentUsers(int currentUsers) {
        Analytics analytics = getAnalytics();
        analytics.updatePeakConcurrentUsers(currentUsers);
        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void resetDailyMetrics() {
        Analytics analytics = getAnalytics();
        analytics.setNewUsersToday(0);
        analyticsRepository.save(analytics);
    }
} 