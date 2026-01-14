package com.datn.viettel.schedule;

import com.datn.viettel.common.Constants;
import com.datn.viettel.services.iservice.SyncConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class SyncConfigProcess {

    private final Environment environment;
    private final SyncConfigService syncConfigService;

    @Autowired
    public SyncConfigProcess(Environment environment,
                             SyncConfigService syncConfigService) {
        this.environment = environment;
        this.syncConfigService = syncConfigService;
    }

    @Scheduled(fixedDelayString = "${scheduled.sync-data.config-to-redis.time-delay}")
    public void syncConfig() {
        log.info("SyncConfigProcess task triggered. Checking active status...");
        long startTime = System.currentTimeMillis();
        try {
            if (!Objects.equals(environment.getProperty("scheduled.sync-data.config-to-redis.active"), Constants.Status.ACTIVE_STR)) {
                return;
            }
            syncConfigService.syncConfig();
        } catch (Exception e) {
            log.error("Error during scheduled sync data config to redis: {}", e.getMessage(), e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Scheduled sync data config to redis completed in {} ms", (endTime - startTime));
        }
    }

}
