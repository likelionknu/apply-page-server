package com.likelionknu.applyserver.auth.controller;

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto;
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto;
import com.likelionknu.applyserver.auth.service.UserService;
import com.likelionknu.applyserver.common.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @DeleteMapping("/me")
    public GlobalResponse<Void> deleteUsersProfile(String email) {
        userService.deleteUsersProfile(email);
        return GlobalResponse.ok();
    }

    @PatchMapping("/me/profile")
    public GlobalResponse<Void> modifyUsersProfile(String email,
                                                   @RequestBody ModifyProfileRequestDto modifyProfileRequestDto) {
        userService.modifyUsersProfile(email, modifyProfileRequestDto);
        return GlobalResponse.ok();
    }

    @GetMapping("/me/profile")
    public GlobalResponse<ProfileResponseDto> getUsersProfile(String email) {
        return GlobalResponse.ok(userService.getUsersProfile(email));
    }
}
