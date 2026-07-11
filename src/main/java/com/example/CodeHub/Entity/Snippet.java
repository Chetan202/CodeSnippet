package com.example.CodeHub.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "snippets", indexes = {
        @Index(name = "idx_snippets_language", columnList = "language"),
        @Index(name = "idx_snippets_title", columnList = "title"),
        @Index(name = "idx_snippets_created_at", columnList = "created_at")
})
public class Snippet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String title;
    private String language;
    private String tags;
    @Column(name = "collection_name")
    private String collectionName;
    @Column(name = "share_token", unique = true)
    private String shareToken;
    @Column(name = "public_snippet", nullable = false)
    private boolean publicSnippet;
    
    @Column(columnDefinition = "TEXT")
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(nullable = false)
    private boolean deleted;
    
    private boolean starred;
    
    public Snippet() {
        this.createdAt = LocalDateTime.now();
        this.starred = false;
    }
    
    public Snippet(String title, String language, String code, User user) {
        this.title = title;
        this.language = language;
        this.code = code;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.createdBy = user != null ? user.getUsername() : null;
        this.updatedBy = this.createdBy;
        this.starred = false;
        this.shareToken = UUID.randomUUID().toString();
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
        if (user != null) {
            if (createdBy == null) {
                createdBy = user.getUsername();
            }
            if (updatedBy == null) {
                updatedBy = user.getUsername();
            }
        }
        if (shareToken == null || shareToken.isBlank()) {
            shareToken = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (user != null) {
            updatedBy = user.getUsername();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    public boolean isPublicSnippet() {
        return publicSnippet;
    }

    public void setPublicSnippet(boolean publicSnippet) {
        this.publicSnippet = publicSnippet;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
