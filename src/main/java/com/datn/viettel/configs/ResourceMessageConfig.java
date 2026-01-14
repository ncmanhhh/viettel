package com.datn.viettel.configs;

import com.datn.viettel.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResourceMessageConfig {
    private static final MessageSource messageSource = createMessageSource();

    private static final Map<String, MessageSourceAccessor> accessorMap = Constants.Language.getLocales()
            .stream()
            .collect(Collectors.toMap(Locale::getLanguage, locale -> new MessageSourceAccessor(messageSource, locale)));

    private ResourceMessageConfig() {
    }

    private static MessageSource createMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/lang");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(10);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    public static String getResourceMessage(String key) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            MessageSourceAccessor accessor = accessorMap.getOrDefault(locale.getLanguage(), accessorMap.get(Constants.Language.EN));
            return accessor.getMessage(key);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return key;
        }
    }

    public static String getResourceMessage(String language, String key) {
        try {
            Locale locale = new Locale(language);
            MessageSourceAccessor accessor = accessorMap
                    .getOrDefault(locale.getLanguage(), accessorMap.get(Constants.Language.EN));
            return accessor.getMessage(key);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return key;
        }
    }
}
