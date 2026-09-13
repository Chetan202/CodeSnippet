package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Entity.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserViewRepository extends JpaRepository<UserView, Long> {
    
    Optional<UserView> findByUserAndViewDate(User user, LocalDate viewDate);
    
    @Query("SELECT COUNT(uv) FROM UserView uv WHERE uv.viewDate = :date")
    Long countByViewDate(@Param("date") LocalDate date);
    
    @Query("SELECT uv.viewDate as date, COUNT(uv) as count FROM UserView uv WHERE uv.viewDate BETWEEN :startDate AND :endDate GROUP BY uv.viewDate ORDER BY uv.viewDate")
    List<Object[]> countViewsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT uv.viewDate as date, COUNT(uv) as count FROM UserView uv GROUP BY uv.viewDate ORDER BY uv.viewDate")
    List<Object[]> countAllViewsByDate();
}
