package com.likelionknu.applyserver.admin.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AdminRecruitSummaryResponse(
        @JsonProperty("recruit_id") Long recruitId,
        String title,
        @JsonProperty("start_at") LocalDateTime startAt,
        @JsonProperty("end_at") LocalDateTime endAt,
        int submit,
        int draft
) {
}