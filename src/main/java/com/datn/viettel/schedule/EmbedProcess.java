package com.datn.viettel.schedule;

import com.datn.viettel.common.Constants;
import com.datn.viettel.services.iservice.EmbedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class EmbedProcess {

    private final Environment environment;
    private final EmbedService embedService;

    @Autowired
    public EmbedProcess(Environment environment, EmbedService embedService) {
        this.environment = environment;
        this.embedService = embedService;
    }

    @Scheduled(fixedDelayString = "${scheduled.embedding.mobile-package.time-delay}")
    public void embedMobilePackages() {
        long startTime = System.currentTimeMillis();
        try {
            if (!Objects.equals(environment.getProperty("scheduled.embedding.mobile-package.active"), Constants.Status.ACTIVE_STR)) {
                return;
            }
            embedService.embedMobilePackagesV2();
        } catch (Exception e) {
            log.error("Error during scheduled embedding of mobile packages: {}", e.getMessage(), e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Scheduled embedding of mobile packages completed in {} ms", (endTime - startTime));
        }
    }

    @Scheduled(fixedDelayString = "${scheduled.embedding.ftth-package.time-delay}")
    public void embedFtthPackages() {
        long startTime = System.currentTimeMillis();
        try {
            if (!Objects.equals(environment.getProperty("scheduled.embedding.ftth-package.active"), Constants.Status.ACTIVE_STR)) {
                return;
            }
            embedService.embedFtthPackages();
        } catch (Exception e) {
            log.error("Error during scheduled embedding of ftth: {}", e.getMessage(), e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Scheduled embedding of ftth completed in {} ms", (endTime - startTime));
        }
    }

//    @Scheduled(fixedDelayString = "${scheduled.embedding.sim.time-delay}")
//    public void embedSim() {
//        long startTime = System.currentTimeMillis();
//        try {
//            if (!Objects.equals(environment.getProperty("scheduled.embedding.sim.active"), Constants.Status.ACTIVE_STR)) {
//                return;
//            }
//            embedService.embedSims();
//        } catch (Exception e) {
//            log.error("Error during scheduled embedding of sim: {}", e.getMessage(), e);
//        } finally {
//            long endTime = System.currentTimeMillis();
//            log.info("Scheduled embedding of sim completed in {} ms", (endTime - startTime));
//        }
//    }

}