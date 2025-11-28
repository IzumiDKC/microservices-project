package com.social.post_service.service;


import com.social.post_service.client.UserClient;
import com.social.post_service.entity.Post;
import com.social.post_service.entity.PostLike;
import com.social.post_service.repository.PostLikeRepository;
import com.social.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepo;
    private final UserClient userClient; // Gọi sang User Service
    private final PostLikeRepository postLikeRepo;

    public PostService(PostRepository postRepo, UserClient userClient, PostLikeRepository postLikeRepo) {
        this.postRepo = postRepo;
        this.userClient = userClient;
        this.postLikeRepo = postLikeRepo;
    }

    public Post createPost(Post post, String username) {
        // Gọi User Service để lấy ID từ database
        Long userId = userClient.getUserIdByUsername(username);
        // Gán ID vào bài viết
        post.setUserId(userId);
        return postRepo.save(post);
    }
    public List<Post> getFeed(Long currentUserId) {
        // Hỏi User Service: "User này đang follow ai?"
        List<Long> followingIds = userClient.getFollowingIds(currentUserId);
        // Thêm chính mình vào để thấy bài của mình
        followingIds.add(currentUserId);
        // Query bài viết
        return postRepo.findByUserIdInOrderByCreatedAtDesc(followingIds);
    }

    public String toggleLike(Long userId, Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (postLikeRepo.existsByPostIdAndUserId(postId, userId)) {
            // Đang like -> Xóa -> Trả về Unlike Success
            PostLike like = postLikeRepo.findByPostIdAndUserId(postId, userId);
            postLikeRepo.delete(like);
            return "Unlike Success";
        } else {
            // Chưa like -> Thêm -> Trả về Like Success
            PostLike newLike = new PostLike(postId, userId);
            postLikeRepo.save(newLike);
            return "Like Success";
        }
    }
}