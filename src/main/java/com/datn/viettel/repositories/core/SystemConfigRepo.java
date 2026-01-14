package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigRepo extends JpaRepository<SystemConfig, Long> {

    @Query("SELECT sc FROM SystemConfig sc WHERE sc.status = 1 AND sc.allowSync = 1")
    List<SystemConfig> findAllSync();

}
