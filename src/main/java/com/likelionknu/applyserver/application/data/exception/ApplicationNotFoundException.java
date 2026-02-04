package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class ApplicationNotFoundException extends GlobalException {
    public ApplicationNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}