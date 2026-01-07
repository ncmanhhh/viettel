package com.datn.viettel.utils.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = InListValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface InList {
    String message() default "Value is not in the list";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] values();
    Class<?> targetType();
}


