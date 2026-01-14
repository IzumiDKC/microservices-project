package com.social.game_service.service;

import com.social.game_service.config.LevelConfig;
import com.social.game_service.entity.Farm;
import com.social.game_service.entity.FarmSlot;
import com.social.game_service.entity.PlantConfig;
import com.social.game_service.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final FarmRepository farmRepository;
    private final PlantDataManager plantDataManager;

    public GameService(FarmRepository farmRepository, PlantDataManager plantDataManager) {
        this.farmRepository = farmRepository;
        this.plantDataManager = plantDataManager;
    }

    public Farm getFarm(String userId) {
        return farmRepository.findByUserId(userId)
                .orElseGet(() -> createNewFarm(userId));
    }

    private Farm createNewFarm(String userId) {
        Farm farm = new Farm();
        farm.setUserId(userId);
        farm.setGold(500);
        farm.setLevel(1);
        farm.setExp(0);

        List<FarmSlot> slots = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            slots.add(new FarmSlot(i, null, 0, false));
        }
        farm.setSlots(slots);
        return farmRepository.save(farm);
    }

    public Farm plantSeed(String userId, int slotId, String seedType) {
        Farm farm = getFarm(userId);
        PlantConfig plant = plantDataManager.getConfig(seedType);

        if (farm.getGold() < plant.getBuyPrice()) {
            throw new RuntimeException("Không đủ vàng! Cần " + plant.getBuyPrice() + " vàng.");
        }

        FarmSlot slot = findSlot(farm, slotId);
        if (slot.getPlantType() != null) {
            throw new RuntimeException("Đất đang có cây!");
        }

        farm.setGold(farm.getGold() - plant.getBuyPrice());

        slot.setPlantType(seedType);
        slot.setPlantedAt(System.currentTimeMillis());
        slot.setWatered(false);

        return farmRepository.save(farm);
    }

    public Farm harvest(String userId, int slotId) {
        Farm farm = getFarm(userId);
        FarmSlot slot = findSlot(farm, slotId);

        if (slot.getPlantType() == null) {
            throw new RuntimeException("Đất trống!");
        }

        PlantConfig type = plantDataManager.getConfig(slot.getPlantType());
        long timeElapsed = System.currentTimeMillis() - slot.getPlantedAt();

        if (timeElapsed < (type.getGrowTime() - 2000)) {
            long secondsLeft = (type.getGrowTime() - timeElapsed) / 1000;
            throw new RuntimeException("Cây chưa chín! Hãy đợi thêm " + secondsLeft + " giây.");
        }

        farm.setGold(farm.getGold() + type.getSellPrice());

        if (farm.getLevel() < LevelConfig.MAX_LEVEL) {
            farm.setExp(farm.getExp() + type.getExpReward());
        }

        int currentLevel = farm.getLevel();
        if (currentLevel < LevelConfig.MAX_LEVEL) {
            int newLevel = LevelConfig.calculateLevel(farm.getExp());

            if (newLevel > currentLevel) {
                farm.setLevel(newLevel);

                if (newLevel == LevelConfig.MAX_LEVEL) {
                    farm.setExp(LevelConfig.LEVEL_THRESHOLDS[LevelConfig.MAX_LEVEL - 1]);
                }

                long totalReward = 0;
                for (int i = currentLevel + 1; i <= newLevel; i++) {
                    totalReward += LevelConfig.getLevelReward(i);
                }

                if (totalReward > 0) {
                    farm.setGold(farm.getGold() + totalReward);
                }
            }
        }

        slot.setPlantType(null);
        slot.setPlantedAt(0);
        slot.setWatered(false);

        return farmRepository.save(farm);
    }

    public Farm removePlant(String userId, int slotId) {
        Farm farm = getFarm(userId);
        FarmSlot slot = findSlot(farm, slotId);

        if (slot.getPlantType() == null) throw new RuntimeException("Không có cây!");

        slot.setPlantType(null);
        slot.setPlantedAt(0);

        return farmRepository.save(farm);
    }

    private FarmSlot findSlot(Farm farm, int slotId) {
        return farm.getSlots().stream()
                .filter(s -> s.getSlotId() == slotId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ô đất không tồn tại"));
    }
}