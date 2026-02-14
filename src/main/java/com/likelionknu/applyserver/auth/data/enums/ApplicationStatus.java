package com.likelionknu.applyserver.auth.data.enums;

public enum ApplicationStatus {
    DRAFT,
    SUBMITTED,
    UNDER_DOCUMENT_REVIEW,
    DOCUMENT_PASSED,
    DOCUMENT_FAILED,
    WAITING_INTERVIEW,
    DONE_INTERVIEW,
    UNDER_INTERVIEW_REVIEW,
    FAIL_INTERVIEW,
    FINAL_PASSED,
    CANCELED;

    public boolean isAfterFinalSubmit() {
        return this != DRAFT && this != CANCELED;
    }

    public boolean isUserLocked() {
        return this != DRAFT;
    }
}