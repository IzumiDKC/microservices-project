package com.social.game_service.controller;

import com.social.game_service.config.LevelConfig;
import com.social.game_service.dto.GameConfigResponse;
import com.social.game_service.entity.Farm;
import com.social.game_service.enums.PlantType;
import com.social.game_service.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/farm")
    public ResponseEntity<Farm> getMyFarm(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(gameService.getFarm(jwt.getSubject()));
    }

    @PostMapping("/plant")
    public ResponseEntity<Farm> plantSeed(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam int slotId,
                                          @RequestParam String seedType) {
        return ResponseEntity.ok(gameService.plantSeed(jwt.getSubject(), slotId, seedType));
    }

    @PostMapping("/harvest")
    public ResponseEntity<Farm> harvest(@AuthenticationPrincipal Jwt jwt, @RequestParam int slotId) {
        return ResponseEntity.ok(gameService.harvest(jwt.getSubject(), slotId));
    }

    @PostMapping("/remove")
    public ResponseEntity<Farm> removePlant(@AuthenticationPrincipal Jwt jwt, @RequestParam int slotId) {
        return ResponseEntity.ok(gameService.removePlant(jwt.getSubject(), slotId));
    }

    @GetMapping("/config")
    public ResponseEntity<GameConfigResponse> getGameConfig() {
        // Chuyển Enum thành Map để FE dễ dùng
        Map<String, PlantType> plantMap = Arrays.stream(PlantType.values())
                .collect(Collectors.toMap(Enum::name, p -> p));

        GameConfigResponse response = GameConfigResponse.builder()
                .plants(plantMap)
                .levels(LevelConfig.LEVEL_THRESHOLDS)
                .rewards(LevelConfig.LEVEL_GOLD_REWARDS)
                .build();

        return ResponseEntity.ok(response);
    }
}