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

    public Follow() {
    }

    public Follow(Long id, Long followerId, Long followingId) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getId() {
        return id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowingId() { // <--- Hàm này fix lỗi dòng 31
        return followingId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}