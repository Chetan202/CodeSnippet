package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.Bookmark;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Bookmark findByUserAndSnippet(User user, Snippet snippet);
    boolean existsByUserAndSnippet(User user, Snippet snippet);
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
    long countBySnippet(Snippet snippet);
}
