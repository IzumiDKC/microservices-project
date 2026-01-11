package com.social.game_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmSlot implements Serializable {
    private int slotId;
    private String plantType;
    private long plantedAt;
    private boolean isWatered;
}