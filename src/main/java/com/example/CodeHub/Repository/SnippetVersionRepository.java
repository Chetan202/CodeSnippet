package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.SnippetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnippetVersionRepository extends JpaRepository<SnippetVersion, Long> {
    List<SnippetVersion> findBySnippetOrderByCreatedAtDesc(Snippet snippet);
}
