package com.likelionknu.applyserver.recruit.controller;

import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse;
import com.likelionknu.applyserver.recruit.service.RecruitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruits")
public class RecruitController {

    private final RecruitService recruitService;

    @GetMapping
    @Operation(summary = "모든 모집 공고 조회")
    public GlobalResponse<List<RecruitListResponse>> getRecruits() {
        return GlobalResponse.ok(recruitService.getRecruits());
    }
}