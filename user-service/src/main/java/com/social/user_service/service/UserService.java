package com.social.user_service.service;

import com.social.user_service.dto.UserResponse;
import com.social.user_service.dto.UserUpdateRequest;
import com.social.user_service.entity.*;
import com.social.user_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AppUserRepository userRepo;
    private final FollowRepository followRepo;
    private final CloudinaryService cloudinaryService;

    public UserService(AppUserRepository userRepo, FollowRepository followRepo, CloudinaryService cloudinaryService) {
        this.userRepo = userRepo;
        this.followRepo = followRepo;
        this.cloudinaryService = cloudinaryService;
    }

    @org.springframework.transaction.annotation.Transactional
    public AppUser syncUser(String username, String firstName, String lastName, String email) {
        AppUser user = userRepo.findByUsername(username).orElse(new AppUser());

        user.setUsername(username);
        user.setEmail(email);

        String fullNameFromKeycloak = "";
        if (firstName != null && !firstName.isEmpty()) fullNameFromKeycloak += firstName + " ";
        if (lastName != null && !lastName.isEmpty()) fullNameFromKeycloak += lastName;
        fullNameFromKeycloak = fullNameFromKeycloak.trim();

        if (!fullNameFromKeycloak.isEmpty()) {
            user.setFullName(fullNameFromKeycloak);
        } else {
            if (user.getFullName() == null || user.getFullName().isEmpty()) {
                user.setFullName(username);
            }
        }

        return userRepo.save(user);
    }

    public UserResponse getUserInfo(String username) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return mapToUserResponse(user);
    }

    public UserResponse getUserById(Long userId) {
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return mapToUserResponse(user);
    }


    public AppUser createUser(AppUser user) {
        return userRepo.save(user);
    }

    // Follow
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (!followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            Follow newFollow = new Follow(null, followerId, followingId);
            followRepo.save(newFollow);
        }
    }

    // Lấy danh sách đang follow
    public List<Long> getFollowingIds(Long userId) {
        return followRepo.findByFollowerId(userId)
                .stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());
    }

    // Tìm kiếm
    public List<AppUser> searchUsers(String keyword) {
        return userRepo.findByUsernameContainingIgnoreCase(keyword);
    }

    public Long getUserIdByUsername(String username) {
        AppUser user = syncUser(username, null, null, null);
        return user.getId();
    }

    // Legacy support
    public String getUsernameById(Long userId) {
        return userRepo.findById(userId).map(AppUser::getUsername).orElse("Unknown");
    }

    public String getAvatarById(Long userId) {
        return userRepo.findById(userId).map(AppUser::getAvatarUrl).orElse(null);
    }

    @Transactional
    public AppUser updateAvatar(String username, MultipartFile file) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String avatarUrl = cloudinaryService.uploadAvatar(file);
        user.setAvatarUrl(avatarUrl);
        return userRepo.save(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, UserUpdateRequest request) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        AppUser savedUser = userRepo.save(user);
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(AppUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());

        // Ưu tiên FullName, fallback về Username
        response.setFullName(user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName()
                : user.getUsername());

        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());

        long followers = followRepo.countByFollowingId(user.getId());
        long following = followRepo.countByFollowerId(user.getId());

        response.setFollowerCount(followers);
        response.setFollowingCount(following);

        return response;
    }

}