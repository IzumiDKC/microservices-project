package com.social.post_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
}