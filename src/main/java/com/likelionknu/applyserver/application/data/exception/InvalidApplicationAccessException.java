package com.likelionknu.applyserver.application.data.exception;


import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class InvalidApplicationAccessException extends GlobalException {
  public InvalidApplicationAccessException() {
    super(ErrorCode.FORBIDDEN);
  }
}