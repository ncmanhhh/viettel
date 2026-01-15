package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.dto.SimDTO;
import com.datn.viettel.entities.core.Sim;
import com.datn.viettel.repositories.core.SimRepository;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.services.iservice.SimService;
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
@Transactional(readOnly = true)
public class SimServiceImpl implements SimService {

    private final SimRepository simRepository;
    private final ElasticsearchService elasticsearchService;
    private final Environment env;

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
    public void toggleStatus(List<UUID> ids) {
        if (com.datn.viettel.utils.DataUtils.isNullOrEmpty(ids)) {
            return;
        }

        List<Sim> sims = simRepository.findAllById(ids);
        if (sims.isEmpty()) {
            return;
        }

        List<String> idsToDeleteFromEs = new java.util.ArrayList<>();
        String indexName = env.getProperty("spring.ai.chat.vector-index.sim");

        for (Sim s : sims) {
            // 1. Toggle Status
            if (Constants.Status.ACTIVE == s.getStatus()) {
                s.setStatus(Constants.Status.INACTIVE);
                idsToDeleteFromEs.add(s.getId().toString());
            } else {
                s.setStatus(Constants.Status.ACTIVE);
            }

            // 2. Reset isEmbed
            s.setIsEmbed(Constants.Status.INACTIVE);
        }

        // 3. Save to DB
        simRepository.saveAll(sims);

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
