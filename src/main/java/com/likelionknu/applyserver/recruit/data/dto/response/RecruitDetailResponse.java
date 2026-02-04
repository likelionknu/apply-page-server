package com.likelionknu.applyserver.recruit.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class RecruitDetailResponse {
    private String title;

    @JsonProperty("start_at")
    private String startAt;

    @JsonProperty("end_at")
    private String endAt;

    private List<RecruitQuestionResponse> questions;
}
