package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.Sim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SimRepository extends JpaRepository<Sim, UUID> {
    List<Sim> findByStatus(Short status);
    List<Sim> findByStatusAndIsEmbed(Short status, Short isEmbed);
}
