package com.datn.viettel.common;

public class ResponseMessage {
    private ResponseMessage(){}

    public static final class Common {
        public static final String SUCCESS = "success";
        public static final String INTERGRATION_SYSTEM_ERROR = "intergration.system.error";
        public static final String INVALID_REQUEST = "invalid.request";
        public static final String PACKAGE_NOT_FOUND = "package.not.found";

        private Common() {
        }
    }

    public static final class Conversation {
        public static final String NOT_FOUND = "conversation.not.found";
        public static final String MISSING_CONTENT = "conversation.missing.prompt";
        public static final String INVALID_CONTENT_LENGTH = "conversation.invalid.content.length";
        public static final String MISSING_ID = "conversation.missing.id";
        public static final String MISSING_RATING = "conversation.missing.rating";
        public static final String INVALID_RATING = "conversation.invalid.rating";

        private Conversation() {
        }
    }

    public static final class Chat {
        private Chat () {}

        public static final String UNSUPPORTED_CHAT_TYPE = "chat.unsupported.chat.type";
    }


    public static final class Chatbot {
        public static final String MISSING_ID = "chatbot.missing.id";

        private Chatbot() {
        }
    }

    public static final class SystemConfig {
        public static final String MISSING_CODE = "system.config.missing.code";

        private SystemConfig() {
        }
    }

    public static final class Integration {
        public static final String REQUEST_OTP_UNSUPPORTED_TYPE = "integration.request.otp.unsupported.type";
        public static final String MISSING_PHONE_NUMBER = "integration.missing.phone-number";
        public static final String MISSING_OTP_TYPE = "integration.missing.otp-type";
        public static final String MISSING_SERVICE_CODE = "integration.missing.service-code";
        public static final String INVALID_LENGTH_PHONE_NUMBER = "integration.invalid.length.phone-number";
        public static final String INVALID_SERVICE_CODE = "integration.invalid.service-code";
        public static final String INVALID_OTP_TYPE = "integration.invalid.otp-type";
        public static final String MISSING_OTP = "integration.missing.otp";
        public static final String INVALID_OTP = "integration.invalid.otp";
        public static final String MISSING_PAYMENT_TYPE = "integration.missing.payment-type";
        public static final String INVALID_PAYMENT_TYPE = "integration.invalid.payment-type";
        public static final String INVALID_PHONE_NUMBER_FORMAT = "integration.invalid.format.phone-number";
        public static final String MISSING_SERVICE_TYPE = "integration.missing.service-type";
        public static final String INVALID_SERVICE_TYPE = "integration.invalid.service-type";
        public static final String OTP_NOT_MATCHED = "integration.otp.not.matched";
        public static final String PRODUCT_NOT_FOUND = "integration.product.not.found";
        public static final String PRODUCT_UNSUPPORTED_TYPE = "integration.product.unsupported.type";
        public static final String SYSTEM_ERROR = "integration.system.error";
        public static final String MISSING_DELIVERY_METHOD = "integration.missing.delivery-method";
        public static final String INVALID_DELIVERY_METHOD = "integration.invalid.delivery-method";
        public static final String MISSING_CUSTOMER_NAME = "integration.missing.customer-name";
        public static final String INVALID_LENGTH_CUSTOMER_NAME = "integration.invalid.length.customer-name";
        public static final String MISSING_PROVINCE = "integration.missing.province";
        public static final String INVALID_PROVINCE = "integration.invalid.province";
        public static final String MISSING_DISTRICT = "integration.missing.district";
        public static final String INVALID_DISTRICT = "integration.invalid.district";
        public static final String MISSING_WARD = "integration.missing.ward";
        public static final String INVALID_WARD = "integration.invalid.ward";
        public static final String MISSING_DETAIL_ADDRESS = "integration.missing.detail-address";
        public static final String INVALID_LENGTH_DETAIL_ADDRESS = "integration.invalid.length.detail-address";
        public static final String MISSING_PRODUCT_ID = "integration.missing.product-id";
        public static final String INVALID_PRODUCT_ID = "integration.invalid.product-id";
        public static final String MISSING_PRODUCT_CODE = "integration.missing.product-code";
        public static final String INVALID_PRODUCT_CODE = "integration.invalid.product-code";
        public static final String MISSING_QUANTITY = "integration.missing.quantity";
        public static final String INVALID_QUANTITY = "integration.invalid.quantity";
        public static final String INVALID_LENGTH_NOTE = "integration.invalid.length.note";
        public static final String CUSTOMER_NOT_FOUND = "integration.customer.not.found";

        private Integration() {
        }
    }


}
