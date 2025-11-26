package com.social.post_service.controller;

import com.social.post_service.entity.Post;
import com.social.post_service.service.PostService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        System.out.println("Nhận request tạo Post: " + post.getContent() + " - UserID: " + post.getUserId());
        return ResponseEntity.ok(postService.createPost(post));
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