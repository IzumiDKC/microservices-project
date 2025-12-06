package com.social.post_service.controller;

import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
// import com.social.post_service.entity.PostReport;
import com.social.post_service.service.CommentService;
import com.social.post_service.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.social.post_service.client.UserClient;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
 //   private final org.camunda.bpm.engine.RuntimeService runtimeService;
 //   private final com.social.post_service.repository.PostReportRepository reportRepo;
    private final CommentService commentService;
    private final UserClient userClient;

    public PostController(PostService postService,
   //                       org.camunda.bpm.engine.RuntimeService runtimeService,
   //                       com.social.post_service.repository.PostReportRepository reportRepo,
                          UserClient userClient,
                          CommentService commentService) {
        this.postService = postService;
     //   this.runtimeService = runtimeService;
     //   this.reportRepo = reportRepo;
        this.userClient = userClient;
        this.commentService = commentService;
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
     /* @PostMapping("/{postId}/report")
    public ResponseEntity<String> reportPost(@PathVariable Long postId,
                                             @RequestParam String reason,
                                             @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {

        // Lấy username từ Token
        String username = jwt.getClaimAsString("preferred_username");
        Long reporterId = userClient.getUserIdByUsername(username);

        PostReport report = new PostReport(postId, reporterId, reason);
        report = reportRepo.save(report);

        // Kích hoạt quy trình Camunda
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("reportId", report.getId());
        variables.put("postId", postId);
        variables.put("reason", reason);
        variables.put("reporterUsername", username); // Thêm username vào biến Camunda

        runtimeService.startProcessInstanceByKey("process_report_post", variables);

        return ResponseEntity.ok("Report submitted. Process ID: " + report.getId());
    }
      */

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                 @RequestBody Comment commentRequest,
                                                 @AuthenticationPrincipal Jwt jwt) {
        // Lấy username từ Token (đã xác thực)
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        commentRequest.setPostId(postId);
        commentRequest.setUserId(userId);

        Comment savedComment = commentService.saveComment(commentRequest);
        return ResponseEntity.ok(savedComment);
    }

    // Lấy danh sách bình luận của 1 bài viết
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // Xóa bình luận
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                                @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Long userId = userClient.getUserIdByUsername(username);

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}