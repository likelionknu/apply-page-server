package com.likelionknu.applyserver.admin.controller;

import com.likelionknu.applyserver.admin.data.dto.request.AdminMemoRequestDto;
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto;
import com.likelionknu.applyserver.admin.service.AdminApplicationService;
import com.likelionknu.applyserver.admin.service.AdminUserService;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto;
import com.likelionknu.applyserver.application.service.ApplicationMailService;
import com.likelionknu.applyserver.application.service.ApplicationService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ApplicationService applicationService;
    private final AdminUserService adminUserService;
    private final ApplicationMailService applicationMailService;
    private final AdminApplicationService adminApplicationService;

    @GetMapping("/application/{id}")
    @Operation(summary = "지원서 상세 정보 조회")
    public GlobalResponse<ApplicationInfoResponseDto> getApplicationInfo(@PathVariable Long id) {
        return GlobalResponse.ok(applicationService.getApplicationInfo(id));
    }

    @GetMapping("/users")
    @Operation(summary = "모든 사용자 목록 조회")
    public GlobalResponse<List<AdminUserResponseDto>> getAllUsers() {
        return GlobalResponse.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/recruits/{id}/notifications/document")
    @Operation(summary = "서류 합격 안내 메일 발송")
    public GlobalResponse<Void> sendDocumentResult(@PathVariable Long id) {
        applicationMailService.sendDocumentResult(SecurityUtil.getUsername(), id);
        return GlobalResponse.ok();
    }

    @GetMapping("/recruits/{id}/notifications/final")
    @Operation(summary = "최종 합격 안내 메일 발송")
    public GlobalResponse<Void> sendFinalResult(@PathVariable Long id) {
        applicationMailService.sendFinalResult(SecurityUtil.getUsername(), id);
        return GlobalResponse.ok();
    }

    @PostMapping("/applications/{id}/memos")
    @Operation(summary = "운영진 메모 등록")
    public GlobalResponse<Void> sendAdminMemo(
            @PathVariable("id") Long id,
            @Valid @RequestBody AdminMemoRequestDto request
    ) {
        adminApplicationService.saveAdminMemo(id, request);
        return GlobalResponse.ok();
    }
}