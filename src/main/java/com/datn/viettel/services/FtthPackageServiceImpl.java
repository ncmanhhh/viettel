package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.FtthPackageDTO;
import com.datn.viettel.entities.core.FtthPackage;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.FtthPackageRepository;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.services.iservice.FtthPackageService;
import com.datn.viettel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
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
    private final ElasticsearchService elasticsearchService;
    private final Environment env;

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
    public void toggleStatus(List<UUID> ids) {
        if (DataUtils.isNullOrEmpty(ids)) {
            return;
        }

        List<FtthPackage> packages = ftthPackageRepository.findAllById(ids);
        if (packages.isEmpty()) {
            return;
        }

        List<String> idsToDeleteFromEs = new java.util.ArrayList<>();
        String indexName = env.getProperty("spring.ai.chat.vector-index.ftth-package");

        for (FtthPackage p : packages) {
            // 1. Toggle Status
            if (STATUS_ACTIVE.equals(p.getStatus())) {
                p.setStatus(Constants.Status.INACTIVE);
                idsToDeleteFromEs.add(p.getId().toString());
            } else {
                p.setStatus(STATUS_ACTIVE);
            }

            // 2. Reset isEmbed
            p.setIsEmbed(NOT_EMBED);
        }

        // 3. Save to DB
        ftthPackageRepository.saveAll(packages);

        // 4. Delete from ES
        if (!idsToDeleteFromEs.isEmpty() && indexName != null) {
            try {
                elasticsearchService.deleteDocumentByIds(indexName, idsToDeleteFromEs);
            } catch (Exception e) {
                // Log error
            }
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

    @Override
    @Transactional
    public FtthPackage create(com.datn.viettel.dto.request.FtthPackageCreateRequest request) {
        FtthPackage existing = ftthPackageRepository.findByCode(request.getCode());
        if (existing != null) {
            throw new LogicException(ResponseMessage.Common.ALREADY_EXISTS, request.getCode());
        }

        FtthPackage ftthPackage = FtthPackage.builder()
                .productId(System.currentTimeMillis())
                .code(request.getCode())
                .price(request.getPrice())
                .promotionPrice(request.getPromotionPrice())
                .speedInText(request.getSpeedInText())
                .speed(request.getSpeed())
                .groupName(request.getGroupName())
                .cycle(request.getCycle())
                .cycleRaw(request.getCycleRaw())
                .promotionVi(request.getPromotionVi())
                .shortDesVi(request.getShortDesVi())
                .priority(request.getPriority())
                .status(request.getStatus())
                .isEmbed(com.datn.viettel.common.Constants.Status.INACTIVE)
                .build();

        ftthPackage.setCreatedAt(java.time.LocalDateTime.now());
        ftthPackage.setUpdatedAt(java.time.LocalDateTime.now());
        ftthPackage.setCreatedBy("ADMIN");
        ftthPackage.setUpdatedBy("ADMIN");

        return ftthPackageRepository.save(ftthPackage);
    }
}
