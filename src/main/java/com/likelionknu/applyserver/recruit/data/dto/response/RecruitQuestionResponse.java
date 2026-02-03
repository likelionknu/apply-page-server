package com.likelionknu.applyserver.recruit.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecruitQuestionResponse {

    private Long id;
    private String question;
    private String savedAnswer;
}