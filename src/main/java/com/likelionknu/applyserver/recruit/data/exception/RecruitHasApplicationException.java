package com.likelionknu.applyserver.recruit.data.exception;

public class RecruitHasApplicationException extends RuntimeException {
    public RecruitHasApplicationException() {
        super("지원자가 존재하여 모집 공고를 수정/삭제할 수 없습니다.");
    }
}