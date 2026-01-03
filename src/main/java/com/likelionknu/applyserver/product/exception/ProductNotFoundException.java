package com.likelionknu.applyserver.product.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class ProductNotFoundException extends GlobalException {
    public ProductNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
