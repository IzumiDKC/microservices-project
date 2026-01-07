package com.social.post_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    private Long userId;
    private String username;
    private String fullName;

    private long likeCount;
    private long commentCount;
    private boolean isLikedByCurrentUser;

    private String avatarUrl;
}