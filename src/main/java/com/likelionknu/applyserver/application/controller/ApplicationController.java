package com.likelionknu.applyserver.application.controller;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse;
import com.likelionknu.applyserver.application.service.*;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import com.likelionknu.applyserver.common.security.exception.AuthenticationInfoException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final ApplicationFinalSubmitService applicationFinalSubmitService;
    private final ApplicationQueryService applicationQueryService;
    private final ApplicationCancelService applicationCancelService;
    private final ApplicationUnsubmitService applicationUnsubmitService;

    @PostMapping
    public ResponseEntity<GlobalResponse<Void>> finalSubmit(
            @RequestBody @Valid FinalSubmitRequestDto request
    ) {
        String email = SecurityUtil.getUsername();
        if (email == null || email.isBlank()) {
            throw new AuthenticationInfoException();
        }

        applicationFinalSubmitService.finalSubmit(email, request);
        return ResponseEntity.ok(GlobalResponse.ok());
    }

    @PutMapping("/drafts/{recruitId}")
    @Operation(summary = "지원서 임시 저장")
    public GlobalResponse<Long> saveDraft(
            @PathVariable Long recruitId,
            @RequestBody List<ApplicationDraftSaveRequest> requests
    ) {
        String email = SecurityUtil.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        Long applicationId = applicationService.saveDraft(user.getId(), recruitId, requests);
        return GlobalResponse.ok(applicationId);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse<List<ApplicationSummaryResponse>>> getMyApplications() {
        String email = SecurityUtil.getUsername();
        if (email == null || email.isBlank()) {
            throw new AuthenticationInfoException();
        }

        List<ApplicationSummaryResponse> responses = applicationQueryService.getMyApplications(email);
        return ResponseEntity.ok(GlobalResponse.ok(responses));
    }

    @PostMapping("/{recruitId}/cancel")
    @Operation(summary = "지원서 회수(지원 취소)")
    public GlobalResponse<Void> cancelApplication(
            @PathVariable Long recruitId
    ) {
        String email = SecurityUtil.getUsername();
        if (email == null || email.isBlank()) {
            throw new AuthenticationInfoException();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        applicationCancelService.cancel(user.getId(), recruitId);
        return GlobalResponse.ok(null);
    }

    @PostMapping("/{recruitId}/restore")
    @Operation(summary = "지원서 회수 취소(상태 복원)")
    public GlobalResponse<Void> restoreApplication(
            @PathVariable Long recruitId
    ) {
        String email = SecurityUtil.getUsername();
        if (email == null || email.isBlank()) {
            throw new AuthenticationInfoException();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        applicationCancelService.restore(user.getId(), recruitId);
        return GlobalResponse.ok(null);
    }

    @PostMapping("/{recruitId}/unsubmit")
    @Operation(summary = "제출 취소 (SUBMITTED → DRAFT)")
    public GlobalResponse<Void> unsubmitApplication(
            @PathVariable Long recruitId
    ) {
        String email = SecurityUtil.getUsername();
        if (email == null || email.isBlank()) {
            throw new AuthenticationInfoException();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        applicationUnsubmitService.unsubmit(user.getId(), recruitId);
        return GlobalResponse.ok(null);
    }
}