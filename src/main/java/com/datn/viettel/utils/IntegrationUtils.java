package com.datn.viettel.utils;


import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class IntegrationUtils {

    private IntegrationUtils() {}

    @SuppressWarnings("unchecked")
    public static String validateResponse(Map<String, Object> response, String functionCode) {
        if (response == null || response.isEmpty()) {
            log.error("Failed to retrieve response [{}]", functionCode);
            throw new IllegalStateException(ResponseMessage.Integration.SYSTEM_ERROR);
        }
        Object statusObj = response.get("errorCode");
        if (statusObj == null || !statusObj.equals("S200")) {
            log.error("Invalid HTTP status or responseCode for register mobile package: {}", statusObj);
            throw new IllegalStateException(ResponseMessage.Integration.SYSTEM_ERROR);
        }
        Object resultObj = response.get("result");
        if (!(resultObj instanceof Map)) {
            log.error("Invalid response register with account format: 'result' is not a Map");
            throw new IllegalStateException(ResponseMessage.Integration.SYSTEM_ERROR);
        }
        Map<String, Object> result = (Map<String, Object>) resultObj;
        String resultErrorCode = (String) result.get("errorCode");
        if (!Constants.ExecutionCode.SUCCESS.equals(resultErrorCode)) {
            String userMsg =  (String) result.get("userMsg");
            return DataUtils.isNullOrBlank(userMsg)
                    ? ResourceMessageConfig.getResourceMessage(ResponseMessage.Integration.SYSTEM_ERROR) : userMsg;
        }
        return ResponseMessage.Common.SUCCESS;
    }

}