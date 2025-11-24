package com.social.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long followerId;
    private Long followingId;

    // --- 1. Constructor mặc định (Bắt buộc cho JPA) ---
    public Follow() {
    }

    // --- 2. Constructor đầy đủ (Thay thế cho @Builder) ---
    public Follow(Long id, Long followerId, Long followingId) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
    }

    // --- 3. Getter (Thay thế cho @Data) ---
    public Long getId() {
        return id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowingId() { // <--- Hàm này fix lỗi dòng 31
        return followingId;
    }

    // --- 4. Setter (Nếu cần) ---
    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}