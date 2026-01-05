package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.SystemConfigValueDTO;

import java.util.List;

public interface SystemConfigService {

    List<SystemConfigValueDTO> getSystemConfigValues(String systemConfigCode);

    List<SystemConfigValueDTO> getAllSystemConfigValues();

}
