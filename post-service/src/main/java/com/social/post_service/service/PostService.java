package com.social.post_service.service;


import com.social.post_service.client.UserClient;
import com.social.post_service.entity.Post;
import com.social.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepo;
    private final UserClient userClient; // Gọi sang User Service

    public PostService(PostRepository postRepo, UserClient userClient) {
        this.postRepo = postRepo;
        this.userClient = userClient;
    }

    public Post createPost(Post post) {
        return postRepo.save(post);
    }

    public List<Post> getFeed(Long currentUserId) {
        // 1. Hỏi User Service: "User này đang follow ai?"
        List<Long> followingIds = userClient.getFollowingIds(currentUserId);

        // 2. Thêm chính mình vào (để thấy bài của mình luôn)
        followingIds.add(currentUserId);

        // 3. Query bài viết
        return postRepo.findByUserIdInOrderByCreatedAtDesc(followingIds);
    }
}