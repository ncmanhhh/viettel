package com.datn.viettel.services.iservice;

import com.datn.viettel.entities.core.MobilePackage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

    public interface MobilePackageService {

        Page<MobilePackage> getMobilePackages(
                Short status,
                Short isEmbed,
                String code,
                String expire,
                int page,
                int size
        );

        /**
         * Lấy chi tiết 1 gói theo ID
         */
        MobilePackage getById(UUID id);


        /**
         * Cập nhật trạng thái gói cước (Batch)
         */
        void toggleStatus(List<UUID> ids);


    
    /**
     * Tạo mới gói cước
     */
    MobilePackage create(com.datn.viettel.dto.request.MobilePackageCreateRequest request);

}
