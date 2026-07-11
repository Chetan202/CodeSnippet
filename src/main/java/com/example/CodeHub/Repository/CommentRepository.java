package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.Comment;
import com.example.CodeHub.Entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySnippetOrderByCreatedAtDesc(Snippet snippet);
    long countBySnippet(Snippet snippet);
}
