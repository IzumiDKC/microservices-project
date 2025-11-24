package com.social.user_service.service;

import com.social.user_service.entity.*;
import com.social.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AppUserRepository userRepo;
    private final FollowRepository followRepo;

    public UserService(AppUserRepository userRepo, FollowRepository followRepo) {
        this.userRepo = userRepo;
        this.followRepo = followRepo;
    }
    // Tạo user mới (để test)
    public AppUser createUser(AppUser user) {
        return userRepo.save(user);
    }

    // Follow user khác
    public void follow(Long followerId, Long followingId) {
        if (!followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            // CŨ (Lỗi): followRepo.save(Follow.builder().followerId(...).build());

            // MỚI (Chuẩn Java): Dùng Constructor thủ công
            // Tham số đầu tiên là ID (để null vì nó tự tăng), sau đó là followerId, followingId
            Follow newFollow = new Follow(null, followerId, followingId);
            followRepo.save(newFollow);
        }
    }
    // API nội bộ: Lấy danh sách ID những người đang follow
    public List<Long> getFollowingIds(Long userId) {
        List<Follow> follows = followRepo.findByFollowerId(userId);
        return follows.stream().map(Follow::getFollowingId).collect(Collectors.toList());
    }
}