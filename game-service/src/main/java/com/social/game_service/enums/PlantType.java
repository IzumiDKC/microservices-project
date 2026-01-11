package com.social.game_service.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlantType {
    CARROT(60 * 1000, 50, 80, 5),
    TOMATO(3 * 60 * 1000, 100, 180, 15),
    CORN(10 * 60 * 1000, 200, 400, 50);

    private final long growTime;
    private final int buyPrice;
    private final int sellPrice;
    private final int expReward;

    PlantType(long growTime, int buyPrice, int sellPrice, int expReward) {
        this.growTime = growTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.expReward = expReward;
    }
}