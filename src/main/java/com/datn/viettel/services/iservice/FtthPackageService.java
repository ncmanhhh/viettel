package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.FtthPackageDTO;
import com.datn.viettel.entities.core.FtthPackage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FtthPackageService {

    /**
     * Lấy danh sách gói FTTH (có phân trang)
     */
    Page<FtthPackage> getFtthPackages(
            Short status,
            Short isEmbed,
            String code,
            String speed,
            String groupName,
            String keyword,
            int page,
            int size
    );


    void toggleStatus(List<UUID> ids);



    /**
     * Tạo mới gói FTTH
     */
    FtthPackage create(com.datn.viettel.dto.request.FtthPackageCreateRequest request);

    /**
     * Lấy chi tiết 1 gói FTTH
     */
    FtthPackageDTO getById(UUID id);


}
