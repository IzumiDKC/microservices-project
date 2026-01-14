package com.social.game_service.config;

public class LevelConfig {

    public static final int MAX_LEVEL = 50;
    public static final long[] LEVEL_THRESHOLDS = new long[MAX_LEVEL];
    public static final long[] LEVEL_GOLD_REWARDS = new long[MAX_LEVEL];

    static {
        long[] initialThresholds = {
                0,      // Lv1
                10,     // Lv2
                50,     // Lv3
                200,    // Lv4
                500,    // Lv5
                1000,   // Lv6
                2000,   // Lv7
                3000,   // Lv8
                5000,   // Lv9
                10000   // Lv10
        };

        long[] initialRewards = {
                0, 100, 200, 300, 500, 700, 1000, 1200, 1300, 1500
        };

        System.arraycopy(initialThresholds, 0, LEVEL_THRESHOLDS, 0, initialThresholds.length);
        System.arraycopy(initialRewards, 0, LEVEL_GOLD_REWARDS, 0, initialRewards.length);


        // Cấp 11 (Index 10): Tăng 2000 so với cấp 10
        long currentDelta = 2000;
        LEVEL_THRESHOLDS[10] = LEVEL_THRESHOLDS[9] + currentDelta;
        LEVEL_GOLD_REWARDS[10] = 1000;

        // Cấp 12 (Index 11): Tăng theo công thức đặc biệt (2000 + 2000*1 = 4000)
        long deltaLv12 = currentDelta + (long)(currentDelta * 1.0);
        LEVEL_THRESHOLDS[11] = LEVEL_THRESHOLDS[10] + deltaLv12;
        LEVEL_GOLD_REWARDS[11] = 1000;

        currentDelta = deltaLv12;

        double multiplier = 0.2;

        for (int i = 12; i < MAX_LEVEL; i++) {
            // Tính lượng EXP cần thêm (Delta mới)
            long additionalExp = (long) (currentDelta * multiplier);
            long newDelta = currentDelta + additionalExp;

            // Cộng dồn vào tổng EXP yêu cầu
            LEVEL_THRESHOLDS[i] = LEVEL_THRESHOLDS[i - 1] + newDelta;

            // Thưởng vàng cố định 1000
            LEVEL_GOLD_REWARDS[i] = 1000;

            // Chuẩn bị cho vòng lặp sau
            currentDelta = newDelta;
            multiplier += 0.1;

            // Giới hạn hệ số tối đa là 3.0
            if (multiplier > 3.0) {
                multiplier = 3.0;
            }
        }
    }

    public static int calculateLevel(long currentExp) {
        int level = 1;
        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (currentExp >= LEVEL_THRESHOLDS[i]) {
                level = i + 1;
            } else {
                break;
            }
        }
        return Math.min(level, MAX_LEVEL);
    }

    // Hàm lấy vàng thưởng
    public static long getLevelReward(int newLevel) {
        int index = newLevel - 1;
        if (index >= 0 && index < LEVEL_GOLD_REWARDS.length) {
            return LEVEL_GOLD_REWARDS[index];
        }
        return 0;
    }
}