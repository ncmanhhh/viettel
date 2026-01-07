package com.datn.viettel.utils.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InListValidator implements ConstraintValidator<InList, Object> {

    private List<Object> validValues;
    private Class<?> targetType;

    @Override
    public void initialize(InList constraintAnnotation) {
        targetType = constraintAnnotation.targetType();
        validValues = Arrays.stream(constraintAnnotation.values())
                .map(this::convertValue)
                .collect(Collectors.toList());
    }

    private Object convertValue(String value) {
        if (targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == String.class) {
            return value;
        } else if (targetType == Short.class) {
            return Short.parseShort(value);
        } else if (targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == Byte.class) {
            return Byte.parseByte(value);
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value != null && validValues.contains(value);
    }

}

