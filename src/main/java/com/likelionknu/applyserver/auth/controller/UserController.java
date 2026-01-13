package com.likelionknu.applyserver.auth.controller;

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto;
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto;
import com.likelionknu.applyserver.auth.service.UserService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
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
    public GlobalResponse<Void> deleteUsersProfile(String email) {
        userService.deleteUsersProfile(email);
        return GlobalResponse.ok();
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "사용자 프로필 등록(변경)")
    public GlobalResponse<ProfileResponseDto> modifyUsersProfile(@RequestParam("email") String email,
                                                   @Valid @RequestBody ModifyProfileRequestDto modifyProfileRequestDto) {
        return GlobalResponse.ok(userService.modifyUsersProfile(email, modifyProfileRequestDto));
    }

    @GetMapping("/me/profile")
    @Operation(summary = "사용자 프로필 조회")
    public GlobalResponse<ProfileResponseDto> getUsersProfile(String email) {
        return GlobalResponse.ok(userService.getUsersProfile(email));
    }
}
