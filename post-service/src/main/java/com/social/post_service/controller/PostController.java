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

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok(postService.createPost(post, username));
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

        long newLikeCount = postService.toggleLike(userId, postId);
        return ResponseEntity.ok(newLikeCount);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                 @RequestBody Comment commentRequest,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        commentRequest.setPostId(postId);
        commentRequest.setUserId(userId);

        Comment savedComment = commentService.saveComment(commentRequest);
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
}