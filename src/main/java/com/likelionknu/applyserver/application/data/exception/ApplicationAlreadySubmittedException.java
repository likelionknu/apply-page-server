package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class ApplicationAlreadySubmittedException extends GlobalException {
    public ApplicationAlreadySubmittedException() {
        super(ErrorCode.CONFLICT);
    }
}
