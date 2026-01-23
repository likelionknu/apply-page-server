package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class RecruitContentNotFoundException extends GlobalException {
    public RecruitContentNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}