package com.social.post_service.repository;

import com.social.post_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Lấy bài viết của danh sách user ID, sắp xếp mới nhất
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
}
