package com.social.post_service.repository;

import com.social.post_service.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    long countByPostId(Long postId);

    @Transactional
    void deleteByPostId(Long postId);
}