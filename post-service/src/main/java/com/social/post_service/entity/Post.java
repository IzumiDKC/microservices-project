package com.social.post_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String imageUrl;
    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // --- Constructor ---
    public Post() {}

    public Post(Long id, String content, String imageUrl, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }


    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}