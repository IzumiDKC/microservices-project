package com.social.user_service.repository;

import com.social.user_service.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Tránh follow trùng lặp
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Post Service lấy danh sách Newsfeed)
    List<Follow> findByFollowerId(Long followerId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowingId(Long followingId);

    long countByFollowerId(Long followerId);
}