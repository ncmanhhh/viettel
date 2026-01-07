package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.HttpLog;
import com.datn.viettel.repositories.core.custom.irepository.HttpLogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpLogRepository extends JpaRepository<HttpLog, Long>, HttpLogCustomRepository {
}