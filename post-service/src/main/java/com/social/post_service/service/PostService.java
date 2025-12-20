package com.social.post_service.service;

import com.social.post_service.client.UserClient;
import com.social.post_service.dto.PostResponse; // Import DTO mới
import com.social.post_service.entity.Post;
import com.social.post_service.entity.PostLike;
import com.social.post_service.repository.CommentRepository; // Import
import com.social.post_service.repository.PostLikeRepository;
import com.social.post_service.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepo;
    private final UserClient userClient;
    private final PostLikeRepository postLikeRepo;
    private final CommentRepository commentRepo;

    public PostService(PostRepository postRepo,
                       UserClient userClient,
                       PostLikeRepository postLikeRepo,
                       CommentRepository commentRepo) {
        this.postRepo = postRepo;
        this.userClient = userClient;
        this.postLikeRepo = postLikeRepo;
        this.commentRepo = commentRepo;
    }

    public Post createPost(Post post, String username) {
        Long userId = userClient.getUserIdByUsername(username);
        post.setUserId(userId);
        return postRepo.save(post);
    }

    public List<PostResponse> getFeed(Long currentUserId) {
        List<Long> followingIds = userClient.getFollowingIds(currentUserId);
        followingIds.add(currentUserId);

        List<Post> posts = postRepo.findByUserIdInOrderByCreatedAtDesc(followingIds);

        return posts.stream().map(post -> {
            PostResponse dto = new PostResponse();

            dto.setId(post.getId());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUserId(post.getUserId());
            dto.setUsername("User " + post.getUserId());

            dto.setLikeCount(post.getLikeCount());
            dto.setCommentCount(post.getCommentCount());

            dto.setLikedByCurrentUser(postLikeRepo.existsByPostIdAndUserId(post.getId(), currentUserId));

            return dto;
        }).collect(Collectors.toList());
    }

    public long toggleLike(Long userId, Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (postLikeRepo.existsByPostIdAndUserId(postId, userId)) {
            PostLike like = postLikeRepo.findByPostIdAndUserId(postId, userId);
            postLikeRepo.delete(like);
            if (post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
            }
        } else {
            PostLike newLike = new PostLike(postId, userId);
            postLikeRepo.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        postRepo.save(post);
        return post.getLikeCount();
    }
}