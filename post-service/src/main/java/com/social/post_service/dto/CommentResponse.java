package com.social.post_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;

    private String username;
    private String fullName;
    private String avatarUrl;

    private Long parentId;
}