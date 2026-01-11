package com.social.game_service.dto;

import com.social.game_service.enums.PlantType;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class GameConfigResponse {
    private Map<String, PlantType> plants;
    private long[] levels;
    private long[] rewards;
}