package com.likelionknu.applyserver.admin.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AdminRecruitApplicationResponse(
        @JsonProperty("application_id") Long applicationId,
        String name,
        String memo,
        String evaluation,
        String status,
        String depart,
        @JsonProperty("submit_at") LocalDateTime submitAt
) {
}