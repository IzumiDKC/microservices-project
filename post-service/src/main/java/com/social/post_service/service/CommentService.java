package com.social.post_service.service;

import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
import com.social.post_service.repository.CommentRepository;
import com.social.post_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
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

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
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