package com.likelionknu.applyserver.admin.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class InvalidRequestException extends GlobalException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}