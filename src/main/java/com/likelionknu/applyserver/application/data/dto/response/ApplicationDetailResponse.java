package com.likelionknu.applyserver.application.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ApplicationDetailResponse {
    private Long applicationId;
    private String recruitTitle;
    private String status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime submittedAt;
    private List<ApplicationAnswerResponse> answers;

    @Getter
    @AllArgsConstructor
    public static class ApplicationAnswerResponse {
        private Long questionId;
        private String question;
        private String answer;
    }
}