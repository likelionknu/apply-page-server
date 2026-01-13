package com.likelionknu.applyserver.common.security.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class JwtAuthenticationException extends GlobalException {
    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}