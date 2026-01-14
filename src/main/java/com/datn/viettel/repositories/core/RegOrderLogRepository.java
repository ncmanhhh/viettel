package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.RegOrderLog;
import com.datn.viettel.repositories.custom.irepository.RegOrderLogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegOrderLogRepository extends JpaRepository<RegOrderLog, Long>, RegOrderLogCustomRepository {
}