package com.likelionknu.applyserver.admin.controller;

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitUpdateRequest;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitApplicationResponse;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponse;
import com.likelionknu.applyserver.admin.service.AdminRecruitApplicationService;
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
    private final AdminRecruitApplicationService adminRecruitApplicationService;

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

    @GetMapping("/{id}/applications")
    @Operation(summary = "모집 공고에 등록된 전체 지원서 조회")
    public GlobalResponse<List<AdminRecruitApplicationResponse>> getRecruitApplications(@PathVariable Long id) {
        return GlobalResponse.ok(adminRecruitApplicationService.getApplicationsByRecruit(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "특정 모집 공고 수정")
    public GlobalResponse<Void> updateRecruit(
            @PathVariable Long id,
            @RequestBody AdminRecruitUpdateRequest request
    ) {
        adminRecruitService.updateRecruit(id, request);
        return GlobalResponse.ok();
    }

    @DeleteMapping("/{id}")
    public GlobalResponse<Void> deleteRecruit(@PathVariable Long id) {
        adminRecruitService.deleteRecruit(id);
        return GlobalResponse.ok();
    }
}