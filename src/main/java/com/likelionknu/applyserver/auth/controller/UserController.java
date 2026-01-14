package com.likelionknu.applyserver.auth.controller;

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto;
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto;
import com.likelionknu.applyserver.auth.service.UserService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @DeleteMapping("/me")
    @Operation(summary = "사용자 회원탈퇴")
    public GlobalResponse<Void> deleteUsersProfile() {
        userService.deleteUsersProfile(SecurityUtil.getUsername());
        return GlobalResponse.ok();
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "사용자 프로필 등록(변경)")
    public GlobalResponse<ProfileResponseDto> modifyUsersProfile(
            @Valid @RequestBody ModifyProfileRequestDto modifyProfileRequestDto) {
        return GlobalResponse.ok(userService.modifyUsersProfile(SecurityUtil.getUsername(), modifyProfileRequestDto));
    }

    @GetMapping("/me/profile")
    @Operation(summary = "사용자 프로필 조회")
    public GlobalResponse<ProfileResponseDto> getUsersProfile() {
        return GlobalResponse.ok(userService.getUsersProfile(SecurityUtil.getUsername()));
    }
}
