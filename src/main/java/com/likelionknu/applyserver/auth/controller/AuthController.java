package com.likelionknu.applyserver.auth.controller;

import com.likelionknu.applyserver.auth.data.dto.response.TokenResponseDto;
import com.likelionknu.applyserver.auth.service.AuthService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "구글 소셜 통합 로그인")
    public GlobalResponse<TokenResponseDto> login(@RequestParam String code) {
        return GlobalResponse.ok(authService.userSocialSignIn(code));
    }

    @PostMapping("/reissue")
    @Operation(summary = "Access Token 재발급")
    public GlobalResponse<TokenResponseDto> reissue(@RequestParam(name = "refresh_token") String refreshToken) {
        return GlobalResponse.ok(authService.reissue(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "사용자 로그아웃")
    public GlobalResponse<Void> logout() {
        authService.userLogout(SecurityUtil.getUsername());
        return GlobalResponse.ok();
    }
}
