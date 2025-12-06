package com.social.post_service.service;

import com.social.post_service.entity.Comment;
import com.social.post_service.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Hàm tạo comment
    public Comment saveComment(Comment comment) {
        // Có thể thêm logic kiểm tra nội dung/tần suất ở đây
        return commentRepository.save(comment);
    }

    // Hàm lấy danh sách comment theo postId
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // Xóa comment
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}