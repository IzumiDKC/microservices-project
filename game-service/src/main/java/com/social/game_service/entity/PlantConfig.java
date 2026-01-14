package com.social.game_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plant_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantConfig {
    @Id
    private String plantType;

    private long growTime;
    private int buyPrice;
    private int sellPrice;
    private int expReward;
    private int totalStages;

    private int unlockLevel;
}