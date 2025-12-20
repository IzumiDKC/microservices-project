package com.social.user_service.repository;

import com.social.user_service.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    java.util.Optional<AppUser> findByUsername(String username);
    List<AppUser> findByUsernameContainingIgnoreCase(String keyword);
}