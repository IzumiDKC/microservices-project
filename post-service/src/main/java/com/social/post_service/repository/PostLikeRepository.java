package com.social.post_service.repository;

import com.social.post_service.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Kiểm tra xem user đã like bài này chưa?
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    // Tìm record like để xóa (khi user unlike)
    PostLike findByPostIdAndUserId(Long postId, Long userId);

    Long countByPostId(Long postId);

    @Transactional
    void deleteByPostId(Long postId);
}