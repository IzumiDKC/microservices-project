package com.social.user_service.service;

import com.social.user_service.entity.*;
import com.social.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

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
    // Tạo user mới
    public AppUser createUser(AppUser user) {
        return userRepo.save(user);
    }

    // Follow user khác
    public void follow(Long followerId, Long followingId) {
        if (!followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            // Tham số đầu tiên là ID, sau đó là followerId, followingId
            Follow newFollow = new Follow(null, followerId, followingId);
            followRepo.save(newFollow);
        }
    }
    // Lấy danh sách ID những người đang follow
    public List<Long> getFollowingIds(Long userId) {
        List<Follow> follows = followRepo.findByFollowerId(userId);
        return follows.stream().map(Follow::getFollowingId).collect(Collectors.toList());
    }

    public List<AppUser> searchUsers(String keyword) {
        return userRepo.findByUsernameContainingIgnoreCase(keyword);
    }

    public Long getUserIdByUsername(String username) {
        // Tìm trong DB xem có user này chưa
        java.util.Optional<AppUser> userOptional = userRepo.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get().getId();
        } else {
            // Chưa có (Lần đầu đăng nhập bằng Keycloak) -> TỰ ĐỘNG TẠO MỚI
            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setFullName(username);

            AppUser savedUser = userRepo.save(newUser);
            return savedUser.getId();
        }
    }
    // Lấy thông tin user từ ID
    public String getUsernameById(Long userId) {
        return userRepo.findById(userId)
                .map(AppUser::getUsername)
                .orElse("Unknown User");
    }

    public AppUser updateAvatar(String username, MultipartFile file) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gọi hàm upload lên mây -> nhận về link https://res.cloudinary.com/...
        String avatarUrl = cloudinaryService.uploadAvatar(file);

        user.setAvatarUrl(avatarUrl);

        return userRepo.save(user);
    }

    public AppUser getUserInfo(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}