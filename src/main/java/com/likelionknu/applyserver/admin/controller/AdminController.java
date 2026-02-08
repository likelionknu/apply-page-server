package com.likelionknu.applyserver.admin.controller;

import com.likelionknu.applyserver.admin.data.dto.request.AdminUserRoleUpdateRequest;
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto;
import com.likelionknu.applyserver.admin.service.AdminUserService;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto;
import com.likelionknu.applyserver.application.service.ApplicationMailService;
import com.likelionknu.applyserver.application.service.ApplicationService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ApplicationService applicationService;
    private final AdminUserService adminUserService;
    private final ApplicationMailService applicationMailService;

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

    @DeleteMapping("/users/{id}")
    @Operation(summary = "특정 사용자 강제 회원탈퇴")
    public GlobalResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return GlobalResponse.ok();
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "특정 사용자 권한 변경")
    public GlobalResponse<Void> updateUserRole(@PathVariable Long id, @RequestBody AdminUserRoleUpdateRequest request) {
        adminUserService.updateUserRole(id, request.getRole());
        return GlobalResponse.ok();
    }
}