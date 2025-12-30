package com.social.post_service.service;

import com.social.post_service.client.UserClient;
import com.social.post_service.dto.CommentResponse;
import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
import com.social.post_service.repository.CommentRepository;
import com.social.post_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserClient userClient;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserClient userClient) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userClient = userClient;
    }

    public Comment saveComment(Comment comment) {
        Comment saved = commentRepository.save(comment);

        Post post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        }
        return saved;
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        return comments.stream().map(comment -> {
            CommentResponse dto = new CommentResponse();
            dto.setId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setUserId(comment.getUserId());
            dto.setPostId(comment.getPostId());
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setParentId(comment.getParentId());
            // Gọi User Service
            try {
                dto.setUsername(userClient.getUsernameById(comment.getUserId()));
                dto.setAvatarUrl(userClient.getAvatarById(comment.getUserId()));
            } catch (Exception e) {
                dto.setUsername("Unknown");
                dto.setAvatarUrl(null);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        Long postId = comment.getPostId();

        commentRepository.delete(comment);

        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            long currentCount = post.getCommentCount();
            if (currentCount > 0) {
                post.setCommentCount(currentCount - 1);
                postRepository.save(post);
            }
        }
    }
}