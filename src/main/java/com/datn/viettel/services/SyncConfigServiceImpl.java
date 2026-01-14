package com.datn.viettel.services;

import com.datn.viettel.entities.core.SystemConfig;
import com.datn.viettel.entities.core.SystemConfigValue;
import com.datn.viettel.repositories.core.SystemConfigRepo;
import com.datn.viettel.repositories.core.SystemConfigValueRepo;
import com.datn.viettel.services.iservice.RedisService;
import com.datn.viettel.services.iservice.SyncConfigService;
import com.datn.viettel.utils.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SyncConfigServiceImpl implements SyncConfigService {

    private final SystemConfigRepo systemConfigRepo;
    private final SystemConfigValueRepo systemConfigValueRepo;
    private final RedisService redisService;

    public SyncConfigServiceImpl(SystemConfigRepo systemConfigRepo,
                                 SystemConfigValueRepo systemConfigValueRepo,
                                 RedisService redisService) {
        this.systemConfigRepo = systemConfigRepo;
        this.systemConfigValueRepo = systemConfigValueRepo;
        this.redisService = redisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncConfig() {
        List<SystemConfig> systemConfigs = systemConfigRepo.findAllSync();
        log.info("Found {} system configs to sync", systemConfigs.size());
        for (SystemConfig systemConfig : systemConfigs) {
            List<SystemConfigValue> systemConfigValues = systemConfigValueRepo.findBySystemConfigId(systemConfig.getId());
            if (systemConfigValues.isEmpty()) {
                log.warn("No values found for system config: {}", systemConfig.getName());
                continue;
            }
            List<Object> values = new ArrayList<>();
            for (SystemConfigValue systemConfigValue : systemConfigValues) {
                if (DataUtils.isNullOrEmpty(systemConfigValue.getLanguage())) {
                    values.add(Map.of("all", Map.of(
                            "name", systemConfigValue.getName(),
                            "value", systemConfigValue.getValue()
                    )));
                } else {
                    values.add(Map.of(systemConfigValue.getLanguage(), Map.of(
                            "name", systemConfigValue.getName(),
                            "value", systemConfigValue.getValue()
                    )));
                }
            }
            redisService.set(systemConfig.getName(), values);
            log.info("Synced config '{}' to Redis with {} values", systemConfig.getName(), values.size());
        }
    }

}
