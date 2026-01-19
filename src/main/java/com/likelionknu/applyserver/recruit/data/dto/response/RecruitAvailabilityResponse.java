package com.likelionknu.applyserver.recruit.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecruitAvailabilityResponse {

    private boolean availableApply;
    private boolean existDraft;
}
