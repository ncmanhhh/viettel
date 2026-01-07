package com.datn.viettel.utils.anotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimits {
    RateLimit[] value();
}
