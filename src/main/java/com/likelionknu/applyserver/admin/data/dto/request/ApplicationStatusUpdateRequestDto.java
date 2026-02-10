package com.likelionknu.applyserver.admin.data.dto.request;

import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequestDto(
        @NotNull
        ApplicationStatus status
) {}
