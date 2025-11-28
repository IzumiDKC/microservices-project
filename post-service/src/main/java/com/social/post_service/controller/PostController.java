package com.social.post_service.controller;

import com.social.post_service.entity.Post;
import com.social.post_service.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(500).body("Lỗi: JWT Token bị null!");
        }
        System.out.println("Token Claims: " + jwt.getClaims());
        String username = jwt.getClaimAsString("preferred_username");
        System.out.println("Username trích xuất được: " + username);
        if (username == null) {
            return ResponseEntity.status(500).body("Lỗi: Không tìm thấy 'preferred_username' trong Token");
        }
        return ResponseEntity.ok(postService.createPost(post, username));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(@RequestParam Long userId) {
        return ResponseEntity.ok(postService.getFeed(userId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestParam Long userId) {
        // Hứng chuỗi kết quả từ Service (Like Success hoặc Unlike Success)
        String result = postService.toggleLike(userId, postId);
        return ResponseEntity.ok(result);
    }
}