package com.likelionknu.applyserver.auth.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class GoogleAuthenticaionFailedException extends GlobalException {
    public GoogleAuthenticaionFailedException() {
        super(ErrorCode.GOOGLE_AUTHENTICATION_FAILED);
    }
}