package com.social.user_service.repository;

import com.social.user_service.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Có thể thêm hàm tìm theo username nếu cần login sau này
    // Optional<AppUser> findByUsername(String username);
}