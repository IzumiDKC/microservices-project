package com.social.user_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Size(min = 2, max = 30, message = "Họ tên phải từ 2 đến 30 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên không được chứa số hoặc ký tự đặc biệt")
    private String fullName;

    @Size(max = 100, message = "Tiểu sử không được vượt quá 100 ký tự")
    private String bio;
}
