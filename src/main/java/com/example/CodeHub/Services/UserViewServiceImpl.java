package com.example.CodeHub.Services;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Entity.UserView;
import com.example.CodeHub.Repository.UserViewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserViewServiceImpl implements UserViewService {

    private final UserViewRepository userViewRepository;

    public UserViewServiceImpl(UserViewRepository userViewRepository) {
        this.userViewRepository = userViewRepository;
    }

    @Override
    public void recordView(User user) {
        if (user == null) {
            return; // Don't record views for anonymous users
        }
        
        LocalDate today = LocalDate.now();
        Optional<UserView> existingView = userViewRepository.findByUserAndViewDate(user, today);
        
        if (existingView.isEmpty()) {
            UserView userView = new UserView(user, today);
            userViewRepository.save(userView);
        }
    }

    @Override
    public Long getViewCountForDate(LocalDate date) {
        return userViewRepository.countByViewDate(date);
    }

    @Override
    public Map<LocalDate, Long> getViewCountsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = userViewRepository.countViewsByDateRange(startDate, endDate);
        Map<LocalDate, Long> viewCounts = new LinkedHashMap<>();
        
        // First populate all dates in the range with zero counts
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            viewCounts.put(currentDate, 0L);
            currentDate = currentDate.plusDays(1);
        }
        
        // Then update with actual counts
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            viewCounts.put(date, count);
        }
        
        return viewCounts;
    }

    @Override
    public Map<LocalDate, Long> getAllViewCounts() {
        List<Object[]> results = userViewRepository.countAllViewsByDate();
        Map<LocalDate, Long> viewCounts = new LinkedHashMap<>();
        
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            viewCounts.put(date, count);
        }
        
        return viewCounts;
    }
}
