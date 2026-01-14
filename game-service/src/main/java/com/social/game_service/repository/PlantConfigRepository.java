package com.social.game_service.repository;

import com.social.game_service.entity.PlantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantConfigRepository extends JpaRepository<PlantConfig, String> {
}