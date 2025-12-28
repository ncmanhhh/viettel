package com.datn.viettel.services.iservice;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void set(String key, Object value);

    void set(String key, Object value, long timeout, TimeUnit unit);

    <T> T get(String key, Class<T> clazz);

    boolean delete(String key);

    boolean hasKey(String key);

    void hSet(String key, String hashKey, Object value);

    <T> T hGet(String key, String hashKey, Class<T> clazz);

    void hDelete(String key, String... hashKeys);

    Set<String> hKeys(String key);

    long incr(String key, long delta);

    long decr(String key, long delta);

    String getConfigValue(String key, String language, String defaultValue);

}
