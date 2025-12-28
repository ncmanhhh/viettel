package com.datn.viettel.services;

import com.datn.viettel.services.iservice.RedisService;
import com.datn.viettel.utils.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;
    private final HashOperations<String, String, Object> hashOps;

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.hashOps  = redisTemplate.opsForHash();
    }

    @Override
    public void set(String key, Object value) {
        valueOps.set(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        valueOps.set(key, value, timeout, unit);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object val = valueOps.get(key);
        if (val == null) return null;
        return (T) val;
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        hashOps.put(key, hashKey, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        Object val = hashOps.get(key, hashKey);
        if (val == null) return null;
        return (T) val;
    }

    @Override
    public void hDelete(String key, String... hashKeys) {
        hashOps.delete(key, (Object[]) hashKeys);
    }

    @Override
    public Set<String> hKeys(String key) {
        return hashOps.keys(key);
    }

    @Override
    public long incr(String key, long delta) {
        Long result = valueOps.increment(key, delta);
        return (result != null ? result : 0L);
    }

    @Override
    public long decr(String key, long delta) {
        Long result = valueOps.increment(key, -delta);
        return (result != null ? result : 0L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getConfigValue(String key, String language, String defaultValue) {
        List<Object> dataList = get(key, List.class);
        if (DataUtils.isNullOrEmpty(dataList)) {
            return defaultValue;
        }
        String currentLanguage = DataUtils.isNullOrBlank(language) ? LocaleContextHolder.getLocale().getLanguage() : language;
        String[] languagesToTry = {currentLanguage};
        for (String lang : languagesToTry) {
            for (Object item : dataList) {
                if (item instanceof Map) {
                    Map<String, Object> languageMap = (Map<String, Object>) item;
                    Object languageData = languageMap.get(lang);
                    if (languageData instanceof Map) {
                        Map<String, Object> prompts = (Map<String, Object>) languageData;
                        Object value = prompts.get("value");
                        if (value instanceof String && !DataUtils.isNullOrEmpty((String) value)) {
                            return (String) value;
                        }
                    }
                }
            }
        }
        return defaultValue;
    }

}
