package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.FtthPackage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FtthPackageRepository extends JpaRepository<FtthPackage, UUID> {
    List<FtthPackage> findByStatusAndIsEmbed(Short status, Short isEmbed, Pageable pageable);
    List<FtthPackage> findByStatus(Short status);
}

