package com.social.user_service.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String bio;
}
