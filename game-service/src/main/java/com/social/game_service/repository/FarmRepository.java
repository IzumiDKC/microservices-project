package com.social.game_service.repository;

import com.social.game_service.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByUserId(String userId);
    boolean existsByUserId(String userId);
}