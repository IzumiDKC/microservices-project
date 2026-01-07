package com.social.post_service.controller;

import com.social.post_service.dto.CommentResponse;
import com.social.post_service.dto.PostResponse;
import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
import com.social.post_service.service.CommentService;
import com.social.post_service.service.PostService;
import com.social.post_service.client.UserClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserClient userClient;

    public PostController(PostService postService,
                          UserClient userClient,
                          CommentService commentService) {
        this.postService = postService;
        this.userClient = userClient;
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Quan trọng: chỉ định kiểu dữ liệu
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file, // Nhận file (có thể null)
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        Post post = new Post();
        post.setContent(content);

        return ResponseEntity.ok(postService.createPost(post, file, username));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long currentUserId = userClient.getUserIdByUsername(username);

        return ResponseEntity.ok(postService.getFeed(currentUserId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Long> likePost(@PathVariable Long postId,
                                         @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);
        long newLikeCount = postService.toggleLike(userId, postId, username);

        return ResponseEntity.ok(newLikeCount);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                 @RequestBody Comment commentRequest,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        Comment savedComment = postService.createComment(
                postId,
                commentRequest.getContent(),
                commentRequest.getParentId(),
                userId,
                username
        );

        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                                @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                             @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");
        Long currentUserId = userClient.getUserIdByUsername(username);

        return ResponseEntity.ok(postService.getPostsByUserId(targetUserId, currentUserId));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId,
                                                      @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long currentUserId = userClient.getUserIdByUsername(username);

        return ResponseEntity.ok(postService.getPostById(postId, currentUserId));
    }
}