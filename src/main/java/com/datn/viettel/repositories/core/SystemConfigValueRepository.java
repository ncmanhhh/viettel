package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.SystemConfigValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigValueRepository extends JpaRepository<SystemConfigValue, Long> {

    @Query("SELECT osv " +
            "FROM SystemConfigValue osv JOIN SystemConfig os ON osv.systemConfigId = os.id " +
            "WHERE os.code = :code AND osv.status = 1 AND os.status = 1 AND osv.language = :language")
    List<SystemConfigValue> findBySystemConfigCode(@NotBlank @Param("code") String code, @NotBlank @Param("language") String language);

    @Query("""
    SELECT osv
    FROM SystemConfigValue osv
    JOIN SystemConfig os ON osv.systemConfigId = os.id
    WHERE osv.status = 1
      AND os.status = 1
      AND osv.language = :language
""")
    List<SystemConfigValue> findAllActive(@Param("language") String language);
}