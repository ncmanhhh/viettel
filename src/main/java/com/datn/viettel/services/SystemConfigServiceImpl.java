package com.datn.viettel.services;

import com.datn.viettel.dto.SystemConfigValueDTO;
import com.datn.viettel.entities.core.SystemConfigValue;
import com.datn.viettel.repositories.core.SystemConfigValueRepository;
import com.datn.viettel.services.iservice.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigValueRepository systemConfigValueRepository;

    @Autowired
    public SystemConfigServiceImpl(SystemConfigValueRepository systemConfigValueRepository) {
        this.systemConfigValueRepository = systemConfigValueRepository;
    }

    @Override
    public List<SystemConfigValueDTO> getSystemConfigValues(String systemConfigCode) {
        return systemConfigValueRepository.findBySystemConfigCode(systemConfigCode, LocaleContextHolder.getLocale().getLanguage()).stream()
                .map(this::parseSystemConfigValueDTO).toList();
    }

    private SystemConfigValueDTO parseSystemConfigValueDTO(SystemConfigValue systemConfigValue) {
        return SystemConfigValueDTO.builder()
                .name(systemConfigValue.getName())
                .value(systemConfigValue.getValue())
                .language(systemConfigValue.getLanguage())
                .build();
    }

    @Override
    public List<SystemConfigValueDTO> getAllSystemConfigValues() {
        return systemConfigValueRepository
                .findAllActive(LocaleContextHolder.getLocale().getLanguage())
                .stream()
                .map(this::parseSystemConfigValueDTO)
                .toList();
    }
}
