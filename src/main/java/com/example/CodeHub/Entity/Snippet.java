package com.example.CodeHub.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "snippets")
public class Snippet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String title;
    private String language;
    
    @Column(columnDefinition = "TEXT")
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDateTime createdAt;
    
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
        this.starred = false;
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
    
    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
