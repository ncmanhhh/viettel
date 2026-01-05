package com.datn.viettel.services;

import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.repositories.core.MobilePackageRepository;
import com.datn.viettel.services.iservice.MobilePackageService;
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
public class MobilePackageServiceImpl implements MobilePackageService {

    private final MobilePackageRepository mobilePackageRepository;

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
    public void toggleStatus(UUID id) {
        int updated = mobilePackageRepository.toggleStatus(id);
        if (updated == 0) {
            throw new IllegalArgumentException("MobilePackage not found: " + id);
        }
    }

}