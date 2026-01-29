package com.datn.viettel.services;


import com.datn.viettel.common.Constants;
import com.datn.viettel.services.iservice.CacheService;
import com.datn.viettel.services.iservice.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisService redisService;

    @Override
    @Cacheable(value = "systemPrompts", key = "#chatType + '_' + #language")
    public String getSystemPrompt(Short chatType, String language) {
        String configKey = getSystemPromptConfigKey(chatType);
        return redisService.getConfigValue(configKey, language, Constants.Common.SYSTEM_PROMPT);
    }

    private String getSystemPromptConfigKey(Short chatType) {
        if (Constants.ServiceType.MOBILE_PACKAGE.equals(chatType)) {
            return Constants.SystemConfig.SYSTEM_PROMPT_MOBILE_PACKAGE;
        } else if (Constants.ServiceType.FTTH_PACKAGE.equals(chatType)) {
            return Constants.SystemConfig.SYSTEM_PROMPT_FTTH_PACKAGE;
        } else if (Constants.ServiceType.SIM.equals(chatType)) {
            return Constants.SystemConfig.SYSTEM_PROMPT_SIM;
        } else {
            throw new IllegalArgumentException("Unsupported chat type: " + chatType);
        }
    }

}
