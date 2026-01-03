package com.likelionknu.applyserver.common.response;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_REQUEST("C400", "잘못된 요청입니다.", 400),
    VALIDATION_ERROR("C4001", "요청 값이 올바르지 않습니다.", 400),
    CONFLICT("C409", "이미 존재하는 데이터입니다.", 409),
    FORBIDDEN("C403", "정보 조회를 위한 권한이 부족합니다.", 403),
    UNAUTHORIZED("C401", "인증되지 않은 사용자입니다.", 401),
    NOT_FOUND("C404", "정보를 찾을 수 없습니다.", 404),
    INTERNAL_SERVER_ERROR("C500", "서버 내부 오류가 발생하였습니다.", 500);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}