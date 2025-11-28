package com.social.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;

    // Constructor rỗng
    public AppUser() {}

    // Constructor full
    public AppUser(Long id, String username, String fullName, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    // Getters (Quan trọng để controller trả về JSON)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }

    public void setId(Long id) { this.id = id; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}