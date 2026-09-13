package com.example.CodeHub.Services;

import com.example.CodeHub.Entity.User;

import java.time.LocalDate;
import java.util.Map;

public interface UserViewService {
    
    /**
     * Record a view for a user on the current date
     * Will only record one view per user per day
     */
    void recordView(User user);
    
    /**
     * Get view count for a specific date
     */
    Long getViewCountForDate(LocalDate date);
    
    /**
     * Get daily view counts for a date range
     * Returns a map with date as key and count as value
     */
    Map<LocalDate, Long> getViewCountsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get all view counts grouped by date
     */
    Map<LocalDate, Long> getAllViewCounts();
}
