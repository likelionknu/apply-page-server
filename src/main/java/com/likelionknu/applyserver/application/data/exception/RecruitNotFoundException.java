package com.likelionknu.applyserver.application.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class RecruitNotFoundException extends GlobalException {
  public RecruitNotFoundException() {
    super(ErrorCode.NOT_FOUND);
  }
}