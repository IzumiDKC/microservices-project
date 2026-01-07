package com.social.post_service.client;

import com.social.post_service.dto.UserDto; // Nhớ import DTO này
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    // Các hàm cũ của bạn
    @GetMapping("/api/users/{userId}/following-ids")
    List<Long> getFollowingIds(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/search")
    Long getUserIdByUsername(@RequestParam("username") String username);

    @GetMapping("/api/users/{id}/username")
    String getUsernameById(@PathVariable("id") Long id);

    @GetMapping("/api/users/{id}/avatar")
    String getAvatarById(@PathVariable("id") Long id);

    @GetMapping("/api/users/id/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}