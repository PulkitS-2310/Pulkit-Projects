package org.Analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    // Custom query methods can be added here if needed
} 