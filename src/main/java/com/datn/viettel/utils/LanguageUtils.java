package com.datn.viettel.utils;

import com.datn.viettel.common.Constants;

import java.util.regex.Pattern;

public class LanguageUtils {

    private static final Pattern VI_PATTERN = Pattern.compile("[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđĐ]");

    private LanguageUtils() {
    }

    public static String detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Constants.Language.EN;
        }
        if (VI_PATTERN.matcher(text.toLowerCase()).find()) {
            return Constants.Language.VI;
        }
        return Constants.Language.EN;
    }

}