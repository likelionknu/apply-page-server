package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class ProfileIncompleteException extends GlobalException {
    public ProfileIncompleteException() {
        super(ErrorCode.FORBIDDEN);
    }
}

