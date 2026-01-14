package com.social.game_service.controller;

import com.social.game_service.config.LevelConfig;
import com.social.game_service.dto.GameConfigResponse;
import com.social.game_service.entity.Farm;
import com.social.game_service.entity.PlantConfig;
import com.social.game_service.service.GameService;
import com.social.game_service.service.PlantDataManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final PlantDataManager plantDataManager; // Inject Manager quản lý dữ liệu cây

    public GameController(GameService gameService, PlantDataManager plantDataManager) {
        this.gameService = gameService;
        this.plantDataManager = plantDataManager;
    }

    // 1. Lấy thông tin nông trại
    @GetMapping("/farm")
    public ResponseEntity<Farm> getMyFarm(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(gameService.getFarm(userId));
    }

    // 2. Trồng cây
    @PostMapping("/plant")
    public ResponseEntity<Farm> plantSeed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam int slotId,
            @RequestParam String seedType) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(gameService.plantSeed(userId, slotId, seedType));
    }

    // 3. Thu hoạch
    @PostMapping("/harvest")
    public ResponseEntity<Farm> harvest(@AuthenticationPrincipal Jwt jwt, @RequestParam int slotId) {
        return ResponseEntity.ok(gameService.harvest(jwt.getSubject(), slotId));
    }

    // 4. Xóa cây (Phá bỏ)
    @PostMapping("/remove")
    public ResponseEntity<Farm> removePlant(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam int slotId) {
        return ResponseEntity.ok(gameService.removePlant(jwt.getSubject(), slotId));
    }

    // 5. Lấy cấu hình Game (Dữ liệu cây từ DB, Level, Thưởng)
    @GetMapping("/config")
    public ResponseEntity<GameConfigResponse> getGameConfig() {
        // Lấy Map<String, PlantConfig> từ Manager (Cache trong RAM)
        Map<String, PlantConfig> plants = plantDataManager.getAllConfigs();

        GameConfigResponse response = GameConfigResponse.builder()
                .plants(plants) // Map này chứa thông tin từ DB (giá, time, totalStages...)
                .levels(LevelConfig.LEVEL_THRESHOLDS)
                .rewards(LevelConfig.LEVEL_GOLD_REWARDS)
                .build();

        return ResponseEntity.ok(response);
    }

    // 6. Reload cấu hình nóng (Dành cho Admin khi sửa DB xong)
    @PostMapping("/admin/reload-config")
    public ResponseEntity<String> reloadConfig() {
        plantDataManager.reloadConfig();
        return ResponseEntity.ok("Đã cập nhật cấu hình cây từ Database thành công!");
    }
}