package com.likelionknu.applyserver.application.data.dto.request;

import lombok.Getter;

@Getter
public class ApplicationDraftSaveRequest {
    private Long questionId;
    private String answer;
}