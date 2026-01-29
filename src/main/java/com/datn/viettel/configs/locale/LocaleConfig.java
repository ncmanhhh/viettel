package com.datn.viettel.configs.locale;

import com.datn.viettel.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

// Cấu hình Locale cho ứng dụng, bao gồm việc thiết lập LocaleResolver và các Interceptor liên quan đến ngôn ngữ
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    // Định nghĩa LocaleResolver sử dụng SessionLocaleResolver với ngôn ngữ mặc định là tiếng Anh
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(Constants.Language.EN));
        return sessionLocaleResolver;
    }

    // Định nghĩa LocaleChangeInterceptor để thay đổi ngôn ngữ dựa trên tham số "lang" trong yêu cầu
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    // Đăng ký các Interceptor vào hệ thống
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new LocaleInterceptor());
    }

}
