package com.datn.viettel.services;

import com.datn.viettel.dto.FtthPackageDTO;
import com.datn.viettel.entities.core.FtthPackage;
import com.datn.viettel.repositories.core.FtthPackageRepository;
import com.datn.viettel.services.iservice.FtthPackageService;
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
public class FtthPackageServiceImpl implements FtthPackageService {

    private static final Short STATUS_ACTIVE = 1;
    private static final Short NOT_EMBED = 0;

    private final FtthPackageRepository ftthPackageRepository;

    @Override
    public Page<FtthPackage> getFtthPackages(
            Short status,
            Short isEmbed,
            String code,
            String speed,
            String groupName,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "priority", "created_at")
        );

        Page<FtthPackage> result = ftthPackageRepository.search(
                status,
                isEmbed,
                code,
                speed,
                groupName,
                keyword,
                pageable
        );

        return result;
    }

    @Override
    @Transactional
    public void toggleStatus(UUID id) {
        int updated = ftthPackageRepository.toggleStatus(id);
        if (updated == 0) {
            throw new IllegalArgumentException("FTTH package not found: " + id);
        }
    }

    @Override
    public FtthPackageDTO getById(UUID id) {
        FtthPackage p = ftthPackageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("FTTH package not found: " + id)
                );

        return toResponse(p);
    }


    /* ================= MAPPER ================= */

    private FtthPackageDTO toResponse(FtthPackage p) {
        return FtthPackageDTO.builder()
                .id(p.getId().toString())
                .code(p.getCode())
                .speed(p.getSpeedInText())
                .price(formatMoney(p.getPrice()))
                .promotionPrice(formatMoney(p.getPromotionPrice()))
                .cycle(p.getCycle())
                .promotion(p.getPromotionVi())
                .description(p.getShortDesVi())
                .group(p.getGroupName())
                .build();
    }

    /* ================= FORMAT ================= */

    private String formatMoney(String value) {
        if (value == null || value.isBlank()) return null;
        long money = Long.parseLong(value);
        return String.format("%,dđ", money).replace(",", ".");
    }
}
