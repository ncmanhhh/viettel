package com.datn.viettel.common;

public class ResponseMessage {
    private ResponseMessage(){}

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

    public static final class Common {
        public static final String SUCCESS = "success";
        public static final String INTERGRATION_SYSTEM_ERROR = "intergration.system.error";

        private Common() {
        }
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

}
