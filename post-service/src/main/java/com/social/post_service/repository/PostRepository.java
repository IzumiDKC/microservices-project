package com.social.post_service.repository;

import com.social.post_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Lấy All User
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
    // Post 1 User
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
