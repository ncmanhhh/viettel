package com.datn.viettel.configs.locale;

import com.datn.viettel.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;
import java.util.Objects;

public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (Objects.nonNull(localeResolver)) {
            Locale locale = request.getLocale();
            if (Objects.isNull(locale) || StringUtils.isBlank(locale.getLanguage())) {
                locale = Locale.forLanguageTag(Constants.Language.EN);
            }
            localeResolver.setLocale(request, response, locale);
        }
        return true;
    }

}
