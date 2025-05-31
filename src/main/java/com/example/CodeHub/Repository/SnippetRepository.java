package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    List<Snippet> findByUser(User user);
    List<Snippet> findAllByOrderByCreatedAtDesc();
    List<Snippet> findAllByOrderByStarredDescCreatedAtDesc();
    List<Snippet> findByUserOrderByStarredDescCreatedAtDesc(User user);
    
    // Pagination methods
    Page<Snippet> findAllByOrderByStarredDescCreatedAtDesc(Pageable pageable);
    Page<Snippet> findByUserOrderByStarredDescCreatedAtDesc(User user, Pageable pageable);
    
    // Search methods
    @Query("SELECT s FROM Snippet s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Snippet> searchSnippets(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT s FROM Snippet s WHERE s.user = :user AND (" +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Snippet> searchUserSnippets(@Param("user") User user, @Param("searchTerm") String searchTerm, Pageable pageable);
}
