package com.example.CodeHub.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "snippet_versions")
public class SnippetVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    private Snippet snippet;

    private String title;
    private String language;
    private String tags;
    private String collectionName;

    @Column(columnDefinition = "TEXT")
    private String code;

    private LocalDateTime createdAt;

    public SnippetVersion() {
    }

    public SnippetVersion(Snippet snippet) {
        this.snippet = snippet;
        this.title = snippet.getTitle();
        this.language = snippet.getLanguage();
        this.tags = snippet.getTags();
        this.collectionName = snippet.getCollectionName();
        this.code = snippet.getCode();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getTags() {
        return tags;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
