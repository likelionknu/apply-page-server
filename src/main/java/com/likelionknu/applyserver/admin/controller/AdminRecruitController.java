package com.likelionknu.applyserver.admin.controller;

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponse;
import com.likelionknu.applyserver.admin.service.AdminRecruitService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/recruits")
public class AdminRecruitController {

    private final AdminRecruitService adminRecruitService;

    @GetMapping("/{id}")
    @Operation(summary = "모집 공고 상세 정보 조회")
    public GlobalResponse<AdminRecruitDetailResponse> getRecruitDetail(@PathVariable Long id) {
        return GlobalResponse.ok(adminRecruitService.getRecruitDetail(id));
    }

    @GetMapping
    @Operation(summary = "모집 공고 현황 조회")
    public GlobalResponse<List<AdminRecruitSummaryResponse>> getRecruitSummaries() {
        return GlobalResponse.ok(adminRecruitService.getRecruitSummaries());
    }
}