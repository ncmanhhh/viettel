package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.SimDTO;
import com.datn.viettel.entities.core.Sim;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SimService {

    Page<Sim> getSims(
            Short status,
            Short isEmbed,
            String phone,
            String simType,
            String numberType,
            String keyword,
            int page,
            int size
    );

    void toggleStatus(List<UUID> ids);


    Sim create(com.datn.viettel.dto.request.SimCreateRequest request);

    Sim getById(UUID id);

}
