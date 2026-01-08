package com.social.user_service.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String bio;

    private String fullName;

    private long followerCount;
    private long followingCount;

    private boolean followedByCurrentUser;}