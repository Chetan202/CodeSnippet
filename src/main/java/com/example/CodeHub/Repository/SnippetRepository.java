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
    List<Snippet> findByUserAndDeletedFalse(User user);
    List<Snippet> findAllByDeletedFalseOrderByCreatedAtDesc();
    List<Snippet> findAllByDeletedFalseOrderByStarredDescCreatedAtDesc();
    List<Snippet> findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(User user);
    Snippet findByShareTokenAndPublicSnippetTrueAndDeletedFalse(String shareToken);
    
    // Pagination methods
    Page<Snippet> findAllByDeletedFalseOrderByStarredDescCreatedAtDesc(Pageable pageable);
    Page<Snippet> findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(User user, Pageable pageable);
    Page<Snippet> findByUserAndPublicSnippetTrueAndDeletedFalseOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Search methods
    @Query("SELECT s FROM Snippet s WHERE s.deleted = false AND (" +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.collectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Snippet> searchSnippets(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT s FROM Snippet s WHERE s.user = :user AND s.deleted = false AND (" +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.collectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Snippet> searchUserSnippets(@Param("user") User user, @Param("searchTerm") String searchTerm, Pageable pageable);

    long countByDeletedFalse();
    long countByPublicSnippetTrueAndDeletedFalse();
    long countByPublicSnippetFalseAndDeletedFalse();
}
