package com.likelionknu.applyserver.application.data.dto.response;

import java.time.LocalDateTime;

public record ApplicationSummaryResponse(
        Long applicationId,
        String recruitTitle,
        String status,
        LocalDateTime startAt,
        LocalDateTime endAt
) {}