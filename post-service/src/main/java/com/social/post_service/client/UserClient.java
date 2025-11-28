package com.social.post_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// name phải trùng với spring.application.name bên User Service
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/{userId}/following-ids")
    List<Long> getFollowingIds(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/search")
    Long getUserIdByUsername(@RequestParam("username") String username);
}