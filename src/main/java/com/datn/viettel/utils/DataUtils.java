package com.datn.viettel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class DataUtils {
    private DataUtils() {
    }

    public static <T> boolean isNullOrEmpty(Map<?, T> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    // Kiểm tra Iterable null hoặc rỗng
    public static <T> boolean isNullOrEmpty(Iterable<T> iterable) {
        return Objects.isNull(iterable) || !iterable.iterator().hasNext();
    }

    // Kiểm tra chuỗi null hoặc rỗng
    public static boolean isNullOrEmpty(CharSequence cs) {
        return Objects.isNull(cs) || StringUtils.isEmpty(cs);
    }

    // Chuyển đổi Object thành String, trả về null nếu obj là null
    public static String parseString(Object obj) {
        return Objects.isNull(obj) ? null : obj.toString();
    }

    // Chuyển đổi Object thành String, trả về defaultValue nếu obj là null
    public static String parseString(Object obj, String defaultValue) {
        return Objects.isNull(obj) ? defaultValue : obj.toString();
    }

    // Chuyển đổi Object thành Long, trả về null nếu obj là null
    public static Long parseLong(Object obj) {
        return Objects.isNull(obj) ? null : Long.parseLong(obj.toString());
    }

    // Chuyển đổi Object thành Integer, trả về null nếu obj là null
    public static Integer parseInteger(Object obj) {
        return Objects.isNull(obj) ? null : Integer.parseInt(obj.toString());
    }

    // Kiểm tra chuỗi null hoặc rỗng (blank)
    public static boolean isNullOrBlank(CharSequence cs) {
        return Objects.isNull(cs) || StringUtils.isBlank(cs);
    }

    // Kiểm tra chuỗi có phải UUID hợp lệ không
    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        try {
            UUID uuid = UUID.fromString(str);
            return str.equalsIgnoreCase(uuid.toString());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Chuyển đổi chuỗi thành UUID, trả về null nếu chuỗi không hợp lệ
    public static UUID parseStringToUUID(String id) {
        if (isNullOrBlank(id)) {
            return null;
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id, e);
            return null;
        }
    }

    // Chuyển đổi Object thành JSON String, trả về null nếu có lỗi
    public static String objectToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static String isBlank(String str, String defaultValue) {
        return isNullOrBlank(str) ? defaultValue : str;
    }

    public static String cleanHtml(String input) {
        if (isNullOrBlank(input)) {
            return "";
        }
        return Jsoup.parse(input).text();
    }
}
