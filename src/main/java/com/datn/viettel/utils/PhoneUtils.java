package com.datn.viettel.utils;

public class PhoneUtils {

    private static final String VN_PHONE_REGEX =
            "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$";

    public static boolean isValid(String phone) {
        if (DataUtils.isNullOrBlank(phone)) return false;
        return phone.matches(VN_PHONE_REGEX);
    }

    public static String normalize(String phone) {
        if (phone.startsWith("+84")) {
            return "0" + phone.substring(3);
        }
        return phone;
    }
}