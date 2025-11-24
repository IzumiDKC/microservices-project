package com.social.user_service.controller;

import com.social.user_service.entity.AppUser;
import com.social.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        return ResponseEntity.ok("Followed success");
    }

    // API này để Post Service gọi sang
    @GetMapping("/{userId}/following-ids")
    public ResponseEntity<List<Long>> getFollowingIds(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowingIds(userId));
    }
}
