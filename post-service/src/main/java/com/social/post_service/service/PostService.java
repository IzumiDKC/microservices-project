package com.social.post_service.service;

import com.social.post_service.client.NotificationClient;
import com.social.post_service.client.UserClient;
import com.social.post_service.dto.NotificationRequest;
import com.social.post_service.dto.PostResponse;
import com.social.post_service.dto.UserDto;
import com.social.post_service.entity.Comment;
import com.social.post_service.entity.Post;
import com.social.post_service.entity.PostLike;
import com.social.post_service.repository.CommentRepository;
import com.social.post_service.repository.PostLikeRepository;
import com.social.post_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final UserClient userClient;
    private final PostLikeRepository postLikeRepo;
    private final CommentRepository commentRepo;
    private final NotificationClient notificationClient;
    private final CloudinaryService cloudinaryService;

    public PostService(PostRepository postRepo,
                       UserClient userClient,
                       PostLikeRepository postLikeRepo,
                       CommentRepository commentRepo,
                       NotificationClient notificationClient,
                       CloudinaryService cloudinaryService
    ) {
        this.postRepo = postRepo;
        this.userClient = userClient;
        this.postLikeRepo = postLikeRepo;
        this.commentRepo = commentRepo;
        this.notificationClient = notificationClient;
        this.cloudinaryService = cloudinaryService;
    }

    public Post createPost(Post post, MultipartFile file, String username) {
        Long userId = userClient.getUserIdByUsername(username);
        post.setUserId(userId);

        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file);
            post.setImageUrl(imageUrl);
        }

        return postRepo.save(post);
    }

    public List<PostResponse> getFeed(Long currentUserId) {
        List<Long> followingIds = userClient.getFollowingIds(currentUserId);
        followingIds.add(currentUserId);

        List<Post> posts = postRepo.findByUserIdInOrderByCreatedAtDesc(followingIds);
        return posts.stream().map(post -> mapToPostResponse(post, currentUserId)).collect(Collectors.toList());
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
        return posts.stream().map(post -> mapToPostResponse(post, currentUserId)).collect(Collectors.toList());
    }

    public PostResponse getPostById(Long postId, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return mapToPostResponse(post, currentUserId);
    }
    private PostResponse mapToPostResponse(Post post, Long currentUserId) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUserId(post.getUserId());

        try {
            UserDto user = userClient.getUserById(post.getUserId());
            dto.setUsername(user.getUsername());
            dto.setAvatarUrl(user.getAvatarUrl());

            String displayName = user.getFullName();

            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = user.getUsername();
            }

            dto.setFullName(displayName);

        } catch (Exception e) {
            // Fallback nếu User Service lỗi
            dto.setUsername("Unknown User");
            dto.setFullName("Unknown User");
            dto.setAvatarUrl(null);
        }

        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setLikedByCurrentUser(postLikeRepo.existsByPostIdAndUserId(post.getId(), currentUserId));
        return dto;
    }
}