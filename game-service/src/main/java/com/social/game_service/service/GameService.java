package com.social.game_service.service;

import com.social.game_service.config.LevelConfig;
import com.social.game_service.entity.Farm;
import com.social.game_service.entity.FarmSlot;
import com.social.game_service.enums.PlantType;
import com.social.game_service.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final FarmRepository farmRepository;

    public GameService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
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
        // Tặng 6 ô đất
        for (int i = 0; i < 6; i++) {
            slots.add(new FarmSlot(i, null, 0, false));
        }
        farm.setSlots(slots);
        return farmRepository.save(farm);
    }

    // --- TRỒNG CÂY (TRỪ TIỀN) ---
    public Farm plantSeed(String userId, int slotId, String seedType) {
        Farm farm = getFarm(userId);
        PlantType plant = PlantType.valueOf(seedType);

        // 1. Kiểm tra tiền
        if (farm.getGold() < plant.getBuyPrice()) {
            throw new RuntimeException("Không đủ vàng! Cần " + plant.getBuyPrice() + " vàng.");
        }

        FarmSlot slot = findSlot(farm, slotId);
        if (slot.getPlantType() != null) {
            throw new RuntimeException("Đất đang có cây!");
        }

        // 2. Trừ tiền và Cập nhật slot
        farm.setGold(farm.getGold() - plant.getBuyPrice());

        slot.setPlantType(seedType);
        slot.setPlantedAt(System.currentTimeMillis());
        slot.setWatered(false);

        return farmRepository.save(farm);
    }

    // --- THU HOẠCH (BÁN LUÔN -> CỘNG TIỀN) ---
    public Farm harvest(String userId, int slotId) {
        Farm farm = getFarm(userId);
        FarmSlot slot = findSlot(farm, slotId);

        if (slot.getPlantType() == null) {
            throw new RuntimeException("Đất trống!");
        }

        PlantType type = PlantType.valueOf(slot.getPlantType());
        long timeElapsed = System.currentTimeMillis() - slot.getPlantedAt();

        // Cho phép sai số 2 giây (2000ms) để đồng bộ với Frontend
        if (timeElapsed < (type.getGrowTime() - 2000)) {
            long secondsLeft = (type.getGrowTime() - timeElapsed) / 1000;
            throw new RuntimeException("Cây chưa chín! Hãy đợi thêm " + secondsLeft + " giây.");
        }

        // 1. Cộng Vàng (Giá bán)
        farm.setGold(farm.getGold() + type.getSellPrice());

        // 2. Cộng EXP (Giữ nguyên)
        farm.setExp(farm.getExp() + type.getExpReward());

        // --- LOGIC MỚI: LEVEL UP & THƯỞNG VÀNG ---
        int currentLevel = farm.getLevel();
        int newLevel = LevelConfig.calculateLevel(farm.getExp());

        if (newLevel > currentLevel) {
            // Cập nhật level mới
            farm.setLevel(newLevel);

            // Tính toán thưởng vàng cho TẤT CẢ các cấp đã vượt qua
            // (Đề phòng trường hợp nhận nhiều EXP 1 lúc nhảy cóc từ Lv1 lên Lv3)
            long totalReward = 0;
            for (int i = currentLevel + 1; i <= newLevel; i++) {
                totalReward += LevelConfig.getLevelReward(i);
            }

            if (totalReward > 0) {
                farm.setGold(farm.getGold() + totalReward);
                // System.out.println("Lên cấp " + newLevel + "! Thưởng: " + totalReward);
            }
        }
        // ------------------------------------------

        // 3. Reset ô đất (Giữ nguyên)
        slot.setPlantType(null);
        slot.setPlantedAt(0);
        slot.setWatered(false);

        return farmRepository.save(farm);
    }

    // --- XÓA CÂY (KHÔNG HOÀN TIỀN) ---
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