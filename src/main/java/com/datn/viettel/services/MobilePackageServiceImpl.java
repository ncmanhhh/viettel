package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.repositories.core.MobilePackageRepository;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.services.iservice.MobilePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobilePackageServiceImpl implements MobilePackageService {

    private final MobilePackageRepository mobilePackageRepository;

    private final ElasticsearchService elasticsearchService;
    private final Environment env;

    @Override
    public Page<MobilePackage> getMobilePackages(
            Short status,
            Short isEmbed,
            String code,
            String expire,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return mobilePackageRepository.search(
                status,
                isEmbed,
                code,
                expire,
                pageable
        );
    }

    @Override
    public MobilePackage getById(UUID id) {
        return mobilePackageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("MobilePackage not found: " + id)
                );
    }

    @Override
    @Transactional
    public void toggleStatus(List<UUID> ids) {
        if (com.datn.viettel.utils.DataUtils.isNullOrEmpty(ids)) {
            return;
        }
        
        List<MobilePackage> mobilePackages = mobilePackageRepository.findAllById(ids);
        if (mobilePackages.isEmpty()) {
            return;
        }

        List<String> idsToDeleteFromEs = new ArrayList<>();
        String indexName = env.getProperty("spring.ai.chat.vector-index.mobile-package");

        for (MobilePackage mobilePackage : mobilePackages) {
            // 1. Toggle Status
            if (Constants.Status.ACTIVE == mobilePackage.getStatus()) {
                mobilePackage.setStatus(Constants.Status.INACTIVE);
                // logic: if inactive -> add to delete list
                idsToDeleteFromEs.add(mobilePackage.getId().toString());
            } else {
                mobilePackage.setStatus(Constants.Status.ACTIVE);
            }

            // 2. Always reset isEmbed = 0 (False) to force re-scan by Scheduler
            mobilePackage.setIsEmbed(Constants.Status.INACTIVE);
        }

        // 3. Save all changes to DB
        mobilePackageRepository.saveAll(mobilePackages);

        // 4. Batch delete from Elasticsearch
        if (!idsToDeleteFromEs.isEmpty() && indexName != null) {
            try {
                elasticsearchService.deleteDocumentByIds(indexName, idsToDeleteFromEs);
            } catch (Exception e) {
                // Log error but allow DB transaction to complete
                 // log.error("Failed to delete documents from Elasticsearch: {}", e.getMessage());
            }
        }
    }

}