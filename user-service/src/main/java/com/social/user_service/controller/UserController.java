package com.social.user_service.controller;

import com.social.user_service.dto.UserResponse;
import com.social.user_service.dto.UserUpdateRequest;
import com.social.user_service.entity.AppUser;
import com.social.user_service.repository.AppUserRepository;
import com.social.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/{userId}/follow/{targetId}")
    public ResponseEntity<?> follow(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.follow(userId, targetId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Followed success"));
    }

    // Post Service gọi sang
    @GetMapping("/{userId}/following-ids")
    public ResponseEntity<List<Long>> getFollowingIds(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowingIds(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Long> getUserIdByUsername(@RequestParam String username) {
        Long userId = userService.getUserIdByUsername(username);
        return ResponseEntity.ok(userId);
    }
    @GetMapping("/search-list")
    public ResponseEntity<List<AppUser>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{userId}/username")
    public String getUsernameById(@PathVariable Long userId) {
        return userService.getUsernameById(userId);
    }
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AppUser> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok(userService.updateAvatar(username, file));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserInfo(
            @PathVariable String username,
            @AuthenticationPrincipal Jwt jwt) {

        String currentUsername = null;
        if (jwt != null) {
            currentUsername = jwt.getClaimAsString("preferred_username");
        }

        return ResponseEntity.ok(userService.getUserInfo(username, currentUsername));
    }

    @GetMapping("/{id}/avatar")
    public String getAvatarById(@PathVariable Long id) {
        return userService.getAvatarById(id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String email = jwt.getClaimAsString("email");

        userService.syncUser(username, firstName, lastName, email);

        // Xem chính mình thì tham số thứ 2 cũng là mình (hoặc null tùy logic, nhưng để username cho an toàn)
        return ResponseEntity.ok(userService.getUserInfo(username, username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        // Update xong trả về info mới -> Cũng cần gọi getUserInfo với 2 tham số bên trong Service
        // Nhưng hàm updateProfile của bạn đang trả về UserResponse, hãy kiểm tra lại Service
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
