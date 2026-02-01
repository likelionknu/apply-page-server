package com.likelionknu.applyserver.admin.controller;

import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto;
import com.likelionknu.applyserver.application.service.ApplicationService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ApplicationService applicationService;

    @GetMapping("/application/{id}")
    public GlobalResponse<ApplicationInfoResponseDto> getApplicationInfo(@PathVariable Long id) {
        return GlobalResponse.ok(applicationService.getApplicationInfo(id));
    }
}
