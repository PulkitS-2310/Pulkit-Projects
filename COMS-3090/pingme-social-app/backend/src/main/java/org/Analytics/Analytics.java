package org.Analytics;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // User metrics
    private int totalUsers;
    private int activeUsers;
    private int newUsersToday;
    
    // Activity metrics
    private int totalPosts;
    private int totalComments;
    private int totalLikes;
    private int totalShares;
    
    // Engagement metrics
    private double averageSessionDuration; // in minutes
    private int totalSessions;
    private int peakConcurrentUsers;
    
    // Timestamp
    private LocalDateTime lastUpdated;
    
    // Constructor
    public Analytics() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public int getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public int getNewUsersToday() {
        return newUsersToday;
    }
    
    public void setNewUsersToday(int newUsersToday) {
        this.newUsersToday = newUsersToday;
    }
    
    public int getTotalPosts() {
        return totalPosts;
    }
    
    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }
    
    public int getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }
    
    public int getTotalLikes() {
        return totalLikes;
    }
    
    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }
    
    public int getTotalShares() {
        return totalShares;
    }
    
    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
    
    public double getAverageSessionDuration() {
        return averageSessionDuration;
    }
    
    public void setAverageSessionDuration(double averageSessionDuration) {
        this.averageSessionDuration = averageSessionDuration;
    }
    
    public int getTotalSessions() {
        return totalSessions;
    }
    
    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }
    
    public int getPeakConcurrentUsers() {
        return peakConcurrentUsers;
    }
    
    public void setPeakConcurrentUsers(int peakConcurrentUsers) {
        this.peakConcurrentUsers = peakConcurrentUsers;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Helper methods for updating metrics
    public void incrementTotalUsers() {
        this.totalUsers++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementActiveUsers() {
        this.activeUsers++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementNewUsersToday() {
        this.newUsersToday++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementTotalPosts() {
        this.totalPosts++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementTotalComments() {
        this.totalComments++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementTotalLikes() {
        this.totalLikes++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementTotalShares() {
        this.totalShares++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void updateSessionMetrics(double sessionDuration) {
        this.totalSessions++;
        this.averageSessionDuration = ((this.averageSessionDuration * (this.totalSessions - 1)) + sessionDuration) / this.totalSessions;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void updatePeakConcurrentUsers(int currentUsers) {
        if (currentUsers > this.peakConcurrentUsers) {
            this.peakConcurrentUsers = currentUsers;
            this.lastUpdated = LocalDateTime.now();
        }
    }
}
