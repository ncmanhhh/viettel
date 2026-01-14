package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.SystemConfigValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigValueRepo extends JpaRepository<SystemConfigValue, Long> {

    @Query("SELECT scv FROM SystemConfigValue scv WHERE scv.systemConfigId = :systemConfigId AND scv.status = 1")
    List<SystemConfigValue> findBySystemConfigId(@Param("systemConfigId") Long systemConfigId);

}
