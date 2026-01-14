package com.datn.viettel.services;

import com.datn.viettel.dto.SimDTO;
import com.datn.viettel.entities.core.Sim;
import com.datn.viettel.repositories.core.SimRepository;
import com.datn.viettel.services.iservice.SimService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimServiceImpl implements SimService {

    private final SimRepository simRepository;

    @Override
    public Page<Sim> getSims(
            Short status,
            Short isEmbed,
            String phone,
            String simType,
            String numberType,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return simRepository.search(
                status,
                isEmbed,
                phone,
                simType,
                numberType,
                keyword,
                pageable
        );
    }

    @Override
    @Transactional
    public void toggleStatus(UUID id) {
        int updated = simRepository.toggleStatus(id);
        if (updated == 0) {
            throw new IllegalArgumentException("Sim not found: " + id);
        }
    }


    @Override
    public Sim getById(UUID id) {
        return simRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sim not found: " + id)
                );
    }



    /* ================== MAPPER ================== */

    private SimDTO toResponse(Sim s) {
        return SimDTO.builder()
                .id(s.getId())
                .phoneNumber(s.getPhoneNumber())
                .simType(s.getSimType())
                .numberType(s.getNumberType())
                .price(s.getPrice())
                .promotionPrice(s.getPromotionPrice())
                .desVi(s.getDesVi())
                .build();
    }
}
