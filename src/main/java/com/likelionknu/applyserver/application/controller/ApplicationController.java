package com.likelionknu.applyserver.application.controller;

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse;
import com.likelionknu.applyserver.application.service.ApplicationFinalSubmitService;
import com.likelionknu.applyserver.application.service.ApplicationQueryService;
import com.likelionknu.applyserver.application.service.ApplicationService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.exception.AuthenticationInfoException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
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

    @PutMapping("/drafts/{id}")
    @Operation(summary = "지원서 임시 저장")
    public GlobalResponse<Void> saveDraft(
            @PathVariable Long id,
            @RequestBody List<ApplicationDraftSaveRequest> requests
    ) {
        String email = SecurityUtil.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        applicationService.saveDraft(user.getId(), id, requests);
        return GlobalResponse.ok(null);
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
}
