package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class ApplicationDraftNotFoundException extends GlobalException {
    public ApplicationDraftNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}