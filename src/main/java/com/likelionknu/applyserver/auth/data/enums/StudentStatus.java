package com.likelionknu.applyserver.auth.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentStatus {
    ATTENDING("재학"),
    LEAVE_OF_ABSENCE("휴학"),
    GRADUATION_DEFERRAL("졸업 유예");

    private final String displayName;
}