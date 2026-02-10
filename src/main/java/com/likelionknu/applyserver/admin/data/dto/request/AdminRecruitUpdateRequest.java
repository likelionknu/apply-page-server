package com.likelionknu.applyserver.admin.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record AdminRecruitUpdateRequest(
        String title,
        @JsonProperty("start_at") LocalDateTime startAt,
        @JsonProperty("end_at") LocalDateTime endAt,
        List<Item> questions
) {
    public record Item(
            String question,
            Integer priority
    ) {}
}