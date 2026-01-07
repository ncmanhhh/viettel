package com.datn.viettel.utils.anotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RateLimits.class)
public @interface RateLimit {
    int limit();
    int duration();
    TimeUnit unit() default TimeUnit.MINUTES;
}

