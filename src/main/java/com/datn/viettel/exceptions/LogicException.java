package com.datn.viettel.exceptions;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import lombok.Getter;

@Getter
public class LogicException extends RuntimeException {

    private final String responseCode;
    private final String description;
    private final String keyMessage;

    public LogicException(String keyMessage) {
        super(ResourceMessageConfig.getResourceMessage(Constants.Language.EN, keyMessage));
        this.keyMessage = keyMessage;
        this.description = ResourceMessageConfig.getResourceMessage(keyMessage);
        this.responseCode = Constants.ExecutionCode.ERROR;
    }
    public LogicException(String keyMessage, Object... params) {
        super(String.format(ResourceMessageConfig.getResourceMessage(Constants.Language.EN, keyMessage), params));
        this.responseCode = Constants.ExecutionCode.ERROR;
        this.keyMessage = keyMessage;
        this.description = String.format(ResourceMessageConfig.getResourceMessage(keyMessage), params);
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.description;
    }

}
