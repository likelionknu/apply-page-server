package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class InvalidApplicationQuestionException extends GlobalException {
    public InvalidApplicationQuestionException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}