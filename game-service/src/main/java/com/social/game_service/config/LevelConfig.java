package com.social.game_service.config;

public class LevelConfig {

    // EXP cần để đạt level tương ứng (Index 0 = Lv1, Index 1 = Lv2...)
    public static final long[] LEVEL_THRESHOLDS = {
            0,      // Lv1
            10,     // Lv2
            50,     // Lv3
            150,    // Lv4
            300,    // Lv5
            500,    // Lv6
            1000,   // Lv7
            2000,   // Lv8
            5000,   // Lv9
            10000   // Lv10
    };

    // Vàng thưởng khi đạt level tương ứng
    // Ví dụ: Lên Lv2 nhận 100 vàng, Lên Lv3 nhận 300 vàng...
    public static final long[] LEVEL_GOLD_REWARDS = {
            0,      // Lv1 (Khởi tạo, không thưởng)
            100,    // Thưởng khi lên Lv2
            300,    // Thưởng khi lên Lv3
            500,    // Thưởng khi lên Lv4
            1000,   // Lv5
            2000,   // Lv6
            5000,   // Lv7
            10000,  // Lv8
            20000,  // Lv9
            50000   // Lv10
    };

    // Hàm tính Level (Giữ nguyên)
    public static int calculateLevel(int currentExp) {
        int level = 1;
        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (currentExp >= LEVEL_THRESHOLDS[i]) {
                level = i + 1;
            } else {
                break;
            }
        }
        return level;
    }

    // Hàm lấy vàng thưởng cho level mới
    public static long getLevelReward(int newLevel) {
        int index = newLevel - 1;
        if (index >= 0 && index < LEVEL_GOLD_REWARDS.length) {
            return LEVEL_GOLD_REWARDS[index];
        }
        return 0;
    }
}