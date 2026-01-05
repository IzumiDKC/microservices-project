package com.social.post_service.service;

import com.social.post_service.client.NotificationClient;
import com.social.post_service.client.UserClient;
import com.social.post_service.dto.NotificationRequest;
import com.social.post_service.dto.PostResponse;
import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
import com.social.post_service.entity.PostLike;
import com.social.post_service.repository.CommentRepository;
import com.social.post_service.repository.PostLikeRepository;
import com.social.post_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final UserClient userClient;
    private final PostLikeRepository postLikeRepo;
    private final CommentRepository commentRepo;
    private final NotificationClient notificationClient;

    public PostService(PostRepository postRepo,
                       UserClient userClient,
                       PostLikeRepository postLikeRepo,
                       CommentRepository commentRepo,
                       NotificationClient notificationClient) {
        this.postRepo = postRepo;
        this.userClient = userClient;
        this.postLikeRepo = postLikeRepo;
        this.commentRepo = commentRepo;
        this.notificationClient = notificationClient;
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
            try {
                String realUsername = userClient.getUsernameById(post.getUserId());
                dto.setUsername(realUsername);
                String avatarUrl = userClient.getAvatarById(post.getUserId());
                dto.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                System.err.println("Error fetching user info for post " + post.getId());
                dto.setUsername("Unknown User");
                dto.setAvatarUrl(null);
            }
            dto.setLikeCount(post.getLikeCount());
            dto.setCommentCount(post.getCommentCount());
            dto.setLikedByCurrentUser(postLikeRepo.existsByPostIdAndUserId(post.getId(), currentUserId));

            return dto;
        }).collect(Collectors.toList());
    }

    public long toggleLike(Long userId, Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (postLikeRepo.existsByPostIdAndUserId(postId, userId)) {
            // UNLIKE
            PostLike like = postLikeRepo.findByPostIdAndUserId(postId, userId);
            postLikeRepo.delete(like);
            if (post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
            }
        } else {
            // LIKE
            PostLike newLike = new PostLike(postId, userId);
            postLikeRepo.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);

            // Gửi thông báo
            if (!post.getUserId().equals(userId)) {
                try {
                    notificationClient.sendNotification(new NotificationRequest(
                            post.getUserId(),
                            userId,
                            username,
                            "đã thích bài viết của bạn.",
                            postId
                    ));
                } catch (Exception e) {
                    System.err.println("Lỗi gửi thông báo like: " + e.getMessage());
                }
            }
        }
        postRepo.save(post);
        return post.getLikeCount();
    }

    public Comment createComment(Long postId, String content, Long parentId, Long currentUserId, String currentUsername) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setContent(content);
        comment.setParentId(parentId);

        Comment savedComment = commentRepo.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepo.save(post);

        // Gửi thông báo
        try {
            if (parentId == null && !post.getUserId().equals(currentUserId)) {
                notificationClient.sendNotification(new NotificationRequest(
                        post.getUserId(),
                        currentUserId,
                        currentUsername,
                        "đã bình luận về bài viết của bạn.",
                        postId
                ));
            }

            if (parentId != null) {
                Comment parentComment = commentRepo.findById(parentId).orElse(null);
                if (parentComment != null && !parentComment.getUserId().equals(currentUserId)) {
                    notificationClient.sendNotification(new NotificationRequest(
                            parentComment.getUserId(),
                            currentUserId,
                            currentUsername,
                            "đã trả lời bình luận của bạn.",
                            postId
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo comment: " + e.getMessage());
        }

        return savedComment;
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: You are not the owner of this post");
        }
        postLikeRepo.deleteByPostId(postId);
        commentRepo.deleteByPostId(postId);

        postRepo.delete(post);
    }

    public List<PostResponse> getPostsByUserId(Long userId, Long currentUserId) {
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream().map(post -> {
            PostResponse dto = new PostResponse();
            dto.setId(post.getId());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUserId(post.getUserId());

            try {
                String realUsername = userClient.getUsernameById(post.getUserId());
                dto.setUsername(realUsername);
                String avatarUrl = userClient.getAvatarById(post.getUserId());
                dto.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                dto.setUsername("Unknown");
            }

            dto.setLikeCount(post.getLikeCount());
            dto.setCommentCount(post.getCommentCount());
            dto.setLikedByCurrentUser(postLikeRepo.existsByPostIdAndUserId(post.getId(), currentUserId));

            return dto;
        }).collect(Collectors.toList());
    }

    public PostResponse getPostById(Long postId, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        //  Map sang DTO
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUserId(post.getUserId());
        dto.setUsername("User " + post.getUserId());

        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());

        dto.setLikedByCurrentUser(postLikeRepo.existsByPostIdAndUserId(postId, currentUserId));

        return dto;
    }
}