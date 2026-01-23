package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class EmptyAnswerException extends GlobalException {

    public EmptyAnswerException() {
        super(ErrorCode.VALIDATION_ERROR);
    }
}