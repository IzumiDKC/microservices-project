package com.social.game_service.service;

import com.social.game_service.entity.PlantConfig;
import com.social.game_service.enums.PlantType;
import com.social.game_service.repository.PlantConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlantDataManager {

    private final PlantConfigRepository repository;

    // Cache lưu trữ trong RAM: Key là Enum, Value là Config
    private final Map<String, PlantConfig> configCache = new HashMap<>();

    public PlantDataManager(PlantConfigRepository repository) {
        this.repository = repository;
    }

    // Tự động chạy khi Server khởi động để load DB vào Cache
    @PostConstruct
    public void loadData() {
        List<PlantConfig> allConfigs = repository.findAll();
        configCache.clear();
        for (PlantConfig cfg : allConfigs) {
            configCache.put(cfg.getPlantType(), cfg);
        }
        System.out.println("--- LOADED " + configCache.size() + " PLANT CONFIGS FROM DB ---");
    }

    // Hàm để các Service khác lấy dữ liệu nhanh
    public PlantConfig getConfig(String plantTypeStr) {
        // Kiểm tra xem có trong Enum không (Optional)
        try {
            PlantType.valueOf(plantTypeStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Loại cây không hợp lệ: " + plantTypeStr);
        }

        PlantConfig config = configCache.get(plantTypeStr);
        if (config == null) {
            // Trường hợp trong Enum có tên nhưng DB chưa có dữ liệu
            throw new RuntimeException("Chưa cấu hình chỉ số cho cây: " + plantTypeStr);
        }
        return config;
    }

    // Hàm lấy toàn bộ config cho API /config
    public Map<String, PlantConfig> getAllConfigs() {
        return configCache;
    }

    // API reload lại data mà không cần restart server (Admin dùng)
    public void reloadConfig() {
        loadData();
    }
}